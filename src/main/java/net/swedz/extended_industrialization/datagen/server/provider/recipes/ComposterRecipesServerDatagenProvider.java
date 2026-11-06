package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.EIMachines;

public final class ComposterRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public ComposterRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output)
	{
		super(registries, output);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		addMachineRecipe(
				"composter/fertilizer", "composted_manure", EIMachines.RecipeTypes.COMPOSTER,
				4, 5 * 20,
				(r) -> r
						.fluidIn(EIFluids.MANURE, 150)
						.itemIn(Items.BONE_MEAL, 1)
						.itemIn(EIItems.MULCH, 1)
						.fluidOut(EIFluids.COMPOSTED_MANURE, 200),
				output
		);
	}
}
