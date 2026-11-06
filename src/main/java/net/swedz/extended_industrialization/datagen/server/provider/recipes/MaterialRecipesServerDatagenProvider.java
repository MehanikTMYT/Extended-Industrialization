package net.swedz.extended_industrialization.datagen.server.provider.recipes;

import net.minecraft.data.recipes.RecipeOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.material.EIMaterialRegistry;
import net.swedz.extended_industrialization.material.EIMaterials;
import net.swedz.tesseract.neoforge.material.Material;

public final class MaterialRecipesServerDatagenProvider extends RecipesServerDatagenProvider
{
	public MaterialRecipesServerDatagenProvider(net.minecraft.core.HolderLookup.Provider registries, RecipeOutput output)
	{
		super(registries, output);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput output)
	{
		for(Material material : EIMaterials.values())
		{
			EIMaterialRegistry.get().createRecipesFor(material, registries, output);
		}
	}
}
