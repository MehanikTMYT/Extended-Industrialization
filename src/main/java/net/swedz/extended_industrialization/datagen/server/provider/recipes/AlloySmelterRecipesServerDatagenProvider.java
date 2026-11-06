package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.materials.Material;
import aztech.modern_industrialization.materials.MaterialRegistry;
import com.google.common.collect.Lists;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.tesseract.neoforge.helper.TagHelper;

import java.util.List;

import static aztech.modern_industrialization.materials.part.MIParts.*;

public final class AlloySmelterRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public AlloySmelterRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output)
	{
		super(registries, output);
	}
	
	private static Ingredient combine(Ingredient... ingredients)
	{
		List<Ingredient> nonNullIngredients = Lists.newArrayList();
		for(var ingredient : ingredients)
		{
			if(ingredient != null)
			{
				nonNullIngredients.add(ingredient);
			}
		}
		if(nonNullIngredients.isEmpty())
		{
			throw new IllegalArgumentException("Cannot combine zero ingredients");
		}
		if(nonNullIngredients.size() == 1)
		{
			return nonNullIngredients.getFirst();
		}
		return CompoundIngredient.of(nonNullIngredients.toArray(Ingredient[]::new));
	}
	
	private void addAlloySmelterRecipes(
			String name,
			Ingredient tinyDustA,
			Ingredient nuggetA,
			Ingredient dustA,
			Ingredient ingotA,
			Ingredient blockA,
			int amountA,
			Ingredient tinyDustB,
			Ingredient nuggetB,
			Ingredient dustB,
			Ingredient ingotB,
			Ingredient blockB,
			int amountB,
			ItemLike resultIngot,
			int amountResult,
			RecipeOutput output
	)
	{
		if(resultIngot == null)
		{
			// Result item does not exist (e.g. Create not installed) - nothing to generate
			return;
		}
		var path = "materials/%s/%s".formatted(name, EIMachines.RecipeTypes.ALLOY_SMELTER.getPath());
		addMachineRecipe(
				path,
				"nugget",
				EIMachines.RecipeTypes.ALLOY_SMELTER,
				4,
				10 * 20,
				(r) -> r
						.itemIn(combine(tinyDustA, nuggetA), amountA * 9, 1)
						.itemIn(combine(tinyDustB, nuggetB), amountB * 9, 1)
						.itemOut(resultIngot, amountResult),
				output
		);
		addMachineRecipe(
				path,
				"ingot",
				EIMachines.RecipeTypes.ALLOY_SMELTER,
				4,
				10 * 20,
				(r) -> r
						.itemIn(combine(dustA, ingotA), amountA, 1)
						.itemIn(combine(dustB, ingotB), amountB, 1)
						.itemOut(resultIngot, amountResult),
				output
		);
		addMachineRecipe(
				path,
				"block",
				EIMachines.RecipeTypes.ALLOY_SMELTER,
				4,
				10 * 20 * 9,
				(r) -> r
						.itemIn(blockA, amountA, 1)
						.itemIn(blockB, amountB, 1)
						.itemOut(resultIngot, amountResult * 9),
				output
		);
	}
	
	private void addAlloySmelterRecipes(Material componentA, int amountA, Material componentB, int amountB, Material result, int amountResult, RecipeOutput output)
	{
		addAlloySmelterRecipes(
				result.name,
				componentA.getPart(TINY_DUST).asIngredient(items),
				componentA.getPart(NUGGET).asIngredient(items),
				componentA.getPart(DUST).asIngredient(items),
				componentA.getPart(INGOT).asIngredient(items),
				componentA.getPart(BLOCK).asIngredient(items),
				amountA,
				componentB.getPart(TINY_DUST).asIngredient(items),
				componentB.getPart(NUGGET).asIngredient(items),
				componentB.getPart(DUST).asIngredient(items),
				componentB.getPart(INGOT).asIngredient(items),
				componentB.getPart(BLOCK).asIngredient(items),
				amountB,
				result.getPart(INGOT),
				amountResult,
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("tin"), 1,
				MaterialRegistry.getMaterial("copper"), 3,
				MaterialRegistry.getMaterial("bronze"), 4,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("lead"), 1,
				MaterialRegistry.getMaterial("antimony"), 1,
				MaterialRegistry.getMaterial("battery_alloy"), 2,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("copper"), 1,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("cupronickel"), 2,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("iron"), 2,
				MaterialRegistry.getMaterial("nickel"), 1,
				MaterialRegistry.getMaterial("invar"), 3,
				output
		);
		addAlloySmelterRecipes(
				MaterialRegistry.getMaterial("gold"), 1,
				MaterialRegistry.getMaterial("silver"), 1,
				MaterialRegistry.getMaterial("electrum"), 2,
				output
		);
		
		addAlloySmelterRecipes(
				"brass",
				tag(TagHelper.itemCommon("tiny_dusts/copper")),
				tag(TagHelper.itemCommon("nuggets/copper")),
				tag(TagHelper.itemCommon("dusts/copper")),
				tag(TagHelper.itemCommon("ingots/copper")),
				tag(TagHelper.itemCommon("storage_blocks/copper")),
				1,
				null,
				tag(TagHelper.itemCommon("nuggets/zinc")),
				null,
				tag(TagHelper.itemCommon("ingots/zinc")),
				tag(TagHelper.itemCommon("storage_blocks/zinc")),
				1,
				BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath("create", "brass_ingot")).orElse(null),
				2,
				output.withConditions(new ModLoadedCondition("create"))
		);
	}
}
