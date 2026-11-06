package net.swedz.extended_industrialization.machines.recipe;

import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import aztech.modern_industrialization.machines.recipe.ProxyableMachineRecipeType;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EITags;
import net.swedz.tesseract.neoforge.compat.mi.recipe.MIMachineRecipeBuilder;
import net.swedz.tesseract.neoforge.helper.RegistryHelper;

import java.util.List;
import java.util.Set;

public final class CanningMachineRecipeType extends ProxyableMachineRecipeType
{
	public CanningMachineRecipeType(Identifier id)
	{
		super(id);
	}
	
	private RecipeHolder<MachineRecipe> generateCannedFood(Item foodItem, FoodProperties food)
	{
		var itemId = BuiltInRegistries.ITEM.getKey(foodItem);
		var id = EI.id("/canning_machine/generated/canned_food/%s/%s".formatted(itemId.getNamespace(), itemId.getPath()));
		
		var recipe = new MIMachineRecipeBuilder(this, 2, 5 * 20);
		
		int count = (int) Math.ceil(food.nutrition() / 2D);
		recipe.itemIn(EIItems.TIN_CAN, count);
		recipe.itemIn(foodItem, 1);
		recipe.itemOut(EIItems.CANNED_FOOD, count);
		var remainingItemStack = ItemStack.EMPTY;
		if(foodItem.components().has(net.minecraft.core.component.DataComponents.USE_REMAINDER))
		{
			remainingItemStack = foodItem.components().get(net.minecraft.core.component.DataComponents.USE_REMAINDER).convertInto().create();
		}
		else
		{
			var foodItemStack = foodItem.getDefaultInstance();
			var remainder = foodItemStack.getItem().getCraftingRemainder();
			if(remainder != null)
			{
				remainingItemStack = remainder.create();
			}
		}
		if(!remainingItemStack.isEmpty())
		{
			recipe.itemOut(remainingItemStack.getItem(), remainingItemStack.getCount());
		}
		
		return new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id), (MachineRecipe) recipe.convert());
	}
	
	private List<RecipeHolder<MachineRecipe>> buildCannedFood()
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		for(var item : BuiltInRegistries.ITEM)
		{
			if(!RegistryHelper.holder(BuiltInRegistries.ITEM, item).is(EITags.GeneratedRecipesBlacklist.CANNING_FOOD))
			{
				var itemStack = item.getDefaultInstance();
				var foodProperties = itemStack.get(net.minecraft.core.component.DataComponents.FOOD);
				if(foodProperties != null && foodProperties.nutrition() > 0)
				{
					var food = itemStack.get(net.minecraft.core.component.DataComponents.FOOD);
					recipes.add(this.generateCannedFood(item, food));
				}
			}
		}
		
		return recipes;
	}
	
	private RecipeHolder<MachineRecipe> generateFillingBucket(FluidStack fluidStack, Identifier itemId, Item fullItem, Item emptyItem)
	{
		var id = EI.id("/canning_machine/generated/filling/%s/%s".formatted(itemId.getNamespace(), itemId.getPath()));
		
		var recipe = new MIMachineRecipeBuilder(this, 2, 5 * 20);
		
		recipe.fluidIn(fluidStack.getFluid(), fluidStack.getAmount());
		recipe.itemIn(emptyItem, 1);
		recipe.itemOut(fullItem, 1);
		
		return new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id), (MachineRecipe) recipe.convert());
	}
	
	private RecipeHolder<MachineRecipe> generateEmptyingBucket(FluidStack fluidStack, Identifier itemId, Item fullItem, Item emptyItem)
	{
		var id = EI.id("/canning_machine/generated/emptying/%s/%s".formatted(itemId.getNamespace(), itemId.getPath()));
		
		var recipe = new MIMachineRecipeBuilder(this, 2, 5 * 20);
		
		recipe.itemIn(fullItem, 1);
		recipe.itemOut(emptyItem, 1);
		recipe.fluidOut(fluidStack.getFluid(), fluidStack.getAmount());
		
		return new RecipeHolder<>(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, id), (MachineRecipe) recipe.convert());
	}
	
	private List<RecipeHolder<MachineRecipe>> buildBucket()
	{
		List<RecipeHolder<MachineRecipe>> recipes = Lists.newArrayList();
		
		Set<Fluid> uniqueFluids = Sets.newHashSet();
		for(var fluid : BuiltInRegistries.FLUID)
		{
			var processedFluid = fluid instanceof FlowingFluid flowingFluid ? flowingFluid.getSource() : fluid;
			if(uniqueFluids.add(processedFluid) && !RegistryHelper.holder(BuiltInRegistries.FLUID, processedFluid).is(EITags.GeneratedRecipesBlacklist.CANNING_BUCKETS))
			{
				var fullItem = processedFluid.getBucket();
				if(fullItem != Items.AIR)
				{
					var fullItemStack = fullItem.getDefaultInstance();
					var emptyItemStack = fullItemStack.getItem().getCraftingRemainder();
					var emptyItem = emptyItemStack == null ? Items.BUCKET : emptyItemStack.create().getItem();
					
					var itemId = BuiltInRegistries.ITEM.getKey(fullItem);
					var fluidStack = new FluidStack(processedFluid, 1000);
					recipes.add(this.generateFillingBucket(fluidStack, itemId, fullItem, emptyItem));
					recipes.add(this.generateEmptyingBucket(fluidStack, itemId, fullItem, emptyItem));
				}
			}
		}
		
		return recipes;
	}
	
	@Override
	protected void fillRecipeList(Level level, List<RecipeHolder<MachineRecipe>> recipeList)
	{
		recipeList.addAll(this.getManagerRecipes(level));
		
		if(EI.config().runtimeGeneratedRecipes().canningMachine())
		{
			recipeList.addAll(this.buildCannedFood());
			recipeList.addAll(this.buildBucket());
		}
	}
}
