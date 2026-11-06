package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import aztech.modern_industrialization.MIFluids;
import aztech.modern_industrialization.machines.init.MIMachineRecipeTypes;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EITags;

public final class NPKProcessingRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public NPKProcessingRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output)
	{
		super(registries, output);
	}
	
	private void addPotassiumChlorideRecipe(String id, ItemLike item, int amount, RecipeOutput output)
	{
		addMachineRecipe(
				"npk_processing", "potassium_chloride_from_%s".formatted(id), MIMachineRecipeTypes.CHEMICAL_REACTOR,
				8, 5 * 20,
				(r) -> r
						.itemIn(item, amount)
						.fluidIn(Fluids.WATER, 500)
						.fluidIn(MIFluids.HYDROCHLORIC_ACID, 100)
						.fluidOut(EIFluids.POTASSIUM_CHLORIDE, 200),
				output
		);
	}
	
	private void addPotassiumChlorideRecipe(String id, TagKey<Item> itemTag, int amount, RecipeOutput output)
	{
		addMachineRecipe(
				"npk_processing", "potassium_chloride_from_%s".formatted(id), MIMachineRecipeTypes.CHEMICAL_REACTOR,
				8, 5 * 20,
				(r) -> r
						.itemIn(itemTag, amount)
						.fluidIn(Fluids.WATER, 500)
						.fluidIn(MIFluids.HYDROCHLORIC_ACID, 100)
						.fluidOut(EIFluids.POTASSIUM_CHLORIDE, 200),
				output
		);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addMachineRecipe(
				"npk_processing", "phosphoric_acid", MIMachineRecipeTypes.CHEMICAL_REACTOR,
				8, 5 * 20,
				(r) -> r
						.itemIn(Items.BONE_MEAL, 1)
						.fluidIn(MIFluids.SULFURIC_ACID, 50)
						.fluidOut(EIFluids.PHOSPHORIC_ACID, 250),
				output
		);
		
		addPotassiumChlorideRecipe("potato", Items.POTATO, 1, output);
		addPotassiumChlorideRecipe("beetroot", Items.BEETROOT, 2, output);
		addPotassiumChlorideRecipe("carrot", Items.CARROT, 3, output);
		addPotassiumChlorideRecipe("granite", EITags.itemCommon("dusts/granite"), 32, output);
		
		addMachineRecipe(
				"npk_processing", "potassium_hydroxide", MIMachineRecipeTypes.CHEMICAL_REACTOR,
				8, 10 * 20,
				(r) -> r
						.fluidIn(EIFluids.POTASSIUM_CHLORIDE, 1000)
						.fluidIn(MIFluids.HYDROGEN, 500)
						.fluidIn(MIFluids.OXYGEN, 500)
						.fluidOut(EIFluids.POTASSIUM_HYDROXIDE, 2000)
						.fluidOut(MIFluids.CHLORINE, 250),
				output
		);
		
		addMachineRecipe(
				"npk_processing", "npk", MIMachineRecipeTypes.CHEMICAL_REACTOR,
				8, 15 * 20,
				(r) -> r
						.fluidIn(MIFluids.NITROGEN, 1000)
						.fluidIn(EIFluids.PHOSPHORIC_ACID, 1000)
						.fluidIn(EIFluids.POTASSIUM_HYDROXIDE, 1000)
						.fluidOut(EIFluids.NPK_FERTILIZER, 2500)
						.fluidOut(Fluids.WATER, 500),
				output
		);
	}
}
