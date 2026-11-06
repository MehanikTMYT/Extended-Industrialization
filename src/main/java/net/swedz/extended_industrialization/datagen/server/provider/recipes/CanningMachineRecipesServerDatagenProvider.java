package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import net.neoforged.neoforge.transfer.item.ItemResource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIMachines;
import net.swedz.extended_industrialization.EITags;

public final class CanningMachineRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public CanningMachineRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output)
	{
		super(registries, output);
	}
	
	private void addFillingAndEmptyingRecipes(String id, FluidStack fluidStack, Item emptyItem, Ingredient fullItemAsInput, ItemStack fullItemAsOutput, RecipeOutput output)
	{
		addMachineRecipe(
				"canning_machine/filling", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.fluidIn(fluidStack.getFluid(), fluidStack.getAmount())
						.itemIn(emptyItem, 1)
						.itemOut(ItemResource.of(fullItemAsOutput), 1, 1f),
				output
		);
		addMachineRecipe(
				"canning_machine/emptying", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.itemIn(fullItemAsInput, 1, 1f)
						.itemOut(emptyItem, 1)
						.fluidOut(fluidStack.getFluid(), fluidStack.getAmount()),
				output
		);
	}
	
	private void addFillingAndEmptyingRecipes(String id, TagKey<Fluid> fluidTag, FluidStack fluidStack, Item emptyItem, Item fullItem, RecipeOutput output)
	{
		addMachineRecipe(
				"canning_machine/filling", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.fluidIn(fluidTag, fluidStack.getAmount())
						.itemIn(emptyItem, 1)
						.itemOut(fullItem, 1),
				output
		);
		addMachineRecipe(
				"canning_machine/emptying", id, EIMachines.RecipeTypes.CANNING_MACHINE,
				2, 5 * 20,
				(r) -> r
						.itemIn(fullItem, 1)
						.itemOut(emptyItem, 1)
						.fluidOut(fluidStack.getFluid(), fluidStack.getAmount()),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addFillingAndEmptyingRecipes(
				"minecraft/water_bottle",
				new FluidStack(Fluids.WATER, 350),
				Items.GLASS_BOTTLE,
				DataComponentIngredient.of(false, DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER), Items.POTION),
				PotionContents.createItemStack(Items.POTION, Potions.WATER),
				output
		);
		addFillingAndEmptyingRecipes(
				"minecraft/honey_bottle",
				EITags.Fluids.HONEY,
				new FluidStack(EIFluids.HONEY.asFluid(), 250),
				Items.GLASS_BOTTLE,
				Items.HONEY_BOTTLE,
				output
		);
	}
}
