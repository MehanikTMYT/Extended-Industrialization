package net.swedz.extended_industrialization.machines.recipe;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.ProxyableMachineRecipeType;
import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.machines.recipe.condition.RuntimeGeneratedFlagProcessCondition;
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder;

import java.util.List;
import java.util.function.Consumer;

public final class BreweryMachineRecipeType extends ProxyableMachineRecipeType
{
	private static String id(Identifier location)
	{
		return "%s/%s".formatted(location.getNamespace(), location.getPath());
	}
	
	private static String id(Item item)
	{
		return id(BuiltInRegistries.ITEM.getKey(item));
	}
	
	private static String idItem(Holder<Item> item)
	{
		return id(item.value());
	}
	
	private static String id(ItemStack stack)
	{
		return id(stack.getItem());
	}
	
	private static String id(Ingredient ingredient)
	{
		return idItem(ingredient.getValues().stream().findFirst().orElseThrow());
	}
	
	private static String idPotion(Holder<Potion> potion)
	{
		return id(potion.unwrapKey().orElseThrow().identifier());
	}
	
	public BreweryMachineRecipeType(Identifier id)
	{
		super(id);
	}
	
	private RecipeHolder<MachineRecipe> generate(Identifier id, Ingredient inputIngredient, Ingredient reagentIngredient, ItemStack outputStack)
	{
		var recipe = new MIMachineRecipeBuilder(this, 4, 5 * 20);
		
		recipe.addCondition(new RuntimeGeneratedFlagProcessCondition());
		
		recipe.itemIn(inputIngredient, 4, 1f);
		recipe.itemIn(reagentIngredient, 1, 1f);
		recipe.fluidIn(EIFluids.BLAZING_ESSENCE, 1);
		recipe.itemOut(outputStack.getItem(), 4, 1f);
		
		return new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id), (MachineRecipe) recipe.convert());
	}
	
	private RecipeHolder<MachineRecipe> generate(Identifier id, ItemStack inputStack, Ingredient reagentIngredient, ItemStack outputStack)
	{
		return this.generate(id, DataComponentIngredient.of(false, inputStack), reagentIngredient, outputStack);
	}
	
	private RecipeHolder<MachineRecipe> generateMix(ItemStack stack, PotionBrewing.Mix<Potion> mix)
	{
		var inputStack = stack.copy();
		inputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(mix.from()));
		var reagentIngredient = mix.ingredient();
		var outputStack = stack.copy();
		outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(mix.to()));
		
		var id = EI.id("/brewery/generated/mix/%s/%s/%s/%s".formatted(
				id(stack),
				idPotion(mix.from()),
				id(reagentIngredient),
				idPotion(mix.to())
		));
		
		return this.generate(id, inputStack, reagentIngredient, outputStack);
	}
	
	private List<RecipeHolder<MachineRecipe>> buildMixes(Level level, PotionBrewing potionBrewing)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(var allowedContainer : potionBrewing.containers)
		{
			for(var stack : allowedContainer.items().map(ItemStack::new).toList())
			{
				for(var mix : potionBrewing.potionMixes)
				{
					if(mix.ingredient().getValues().size() > 0)
					{
						recipes.add(this.generateMix(stack, mix));
					}
				}
			}
		}
		
		return recipes;
	}
	
	private RecipeHolder<MachineRecipe> generateContainer(PotionBrewing.Mix<Item> mix, Holder<Potion> potion)
	{
		var inputStack = new ItemStack(mix.from().value());
		inputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
		var reagentIngredient = mix.ingredient();
		var outputStack = new ItemStack(mix.to().value());
		outputStack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
		
		var id = EI.id("/brewery/generated/container/%s/%s/%s/%s".formatted(
				id(potion.unwrapKey().orElseThrow().identifier()),
				idItem(mix.from()),
				id(reagentIngredient),
				idItem(mix.to())
		));
		
		return this.generate(id, inputStack, reagentIngredient, outputStack);
	}
	
	private List<RecipeHolder<MachineRecipe>> buildContainers(Level level, PotionBrewing potionBrewing)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(var mix : potionBrewing.containerMixes)
		{
			if(mix.ingredient().getValues().size() > 0)
			{
				Consumer<Holder<Potion>> recipeGen = (entry) ->
				{
					Potion potion = entry.value();
					if(potion == null || !potionBrewing.isBrewablePotion(entry))
					{
						return;
					}
					recipes.add(this.generateContainer(mix, entry));
				};
				
				if(mix.from().value() instanceof PotionItem)
				{
					BuiltInRegistries.POTION.listElements().forEach(recipeGen);
				}
				else
				{
					recipeGen.accept(BuiltInRegistries.POTION.wrapAsHolder(Potions.AWKWARD.value()));
				}
			}
		}
		
		return recipes;
	}
	
	private RecipeHolder<MachineRecipe> generateModded(ItemStack inputStack, Ingredient reagentIngredient, ItemStack outputStack)
	{
		var id = EI.id("/brewery/generated/modded_improper/%s/%s/%s".formatted(
				id(inputStack),
				id(reagentIngredient),
				id(outputStack)
		));
		
		if(inputStack.has(DataComponents.POTION_CONTENTS) && outputStack.has(DataComponents.POTION_CONTENTS))
		{
			var inputPotion = inputStack.get(DataComponents.POTION_CONTENTS).potion();
			var outputPotion = outputStack.get(DataComponents.POTION_CONTENTS).potion();
			if(inputPotion.isPresent() && outputPotion.isPresent())
			{
				id = EI.id("/brewery/generated/modded/%s/%s/%s".formatted(
						idPotion(inputPotion.get()),
						id(reagentIngredient),
						idPotion(outputPotion.get())
				));
			}
		}
		
		return this.generate(id, inputStack, reagentIngredient, outputStack);
	}
	
	private List<RecipeHolder<MachineRecipe>> generateModded(BrewingRecipe brewingRecipe)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(var inputHolder : brewingRecipe.getInput().items().toList())
		{
			var inputStack = new ItemStack(inputHolder.value());
			var reagentIngredient = brewingRecipe.getIngredient();
			var reagentStack = reagentIngredient.items().findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY);
			var outputStack = brewingRecipe.getOutput(inputStack, reagentStack);
			recipes.add(this.generateModded(inputStack, reagentIngredient, outputStack));
		}
		
		return recipes;
	}
	
	private List<RecipeHolder<MachineRecipe>> buildModded(Level level, PotionBrewing potionBrewing)
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(var entry : potionBrewing.getRecipes())
		{
			if(entry instanceof BrewingRecipe brewingRecipe)
			{
				recipes.addAll(this.generateModded(brewingRecipe));
			}
		}
		
		return recipes;
	}
	
	@Override
	protected void fillRecipeList(Level level, List<RecipeHolder<MachineRecipe>> recipeList)
	{
		recipeList.addAll(this.getManagerRecipes(level));
		
		recipeList.addAll(this.buildMixes(level, level.potionBrewing()));
		recipeList.addAll(this.buildContainers(level, level.potionBrewing()));
		recipeList.addAll(this.buildModded(level, level.potionBrewing()));
	}
}
