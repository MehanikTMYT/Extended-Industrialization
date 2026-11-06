package net.swedz.extended_industrialization.compat.viewer.common;

import aztech.modern_industrialization.client.compat.viewer.abstraction.ViewerCategory;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.material.Fluid;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.datamap.FertilizerPotency;

import java.util.function.Consumer;

public final class FluidFertilizerCategory extends ViewerCategory<Fluid>
{
	public FluidFertilizerCategory()
	{
		super(Fluid.class, EI.id("fluid_fertilizers"), EI.text().fluidFertilizers(), EIFluids.NPK_FERTILIZER.asFluid().getBucket().getDefaultInstance(), 150, 45);
	}
	
	@Override
	public Identifier getRecipeId(Fluid fluid)
	{
		var key = BuiltInRegistries.FLUID.getKey(fluid);
		return EI.id("/fluid_fertilizer/%s/%s".formatted(key.getNamespace(), key.getPath()));
	}
	
	@Override
	public void buildWorkstations(WorkstationConsumer consumer)
	{
		consumer.accept(EIItems.valueOf("electric_farmer"));
	}
	
	@Override
	public void buildRecipes(net.minecraft.world.item.crafting.RecipeMap recipeManager, RegistryAccess registryAccess, Consumer<Fluid> consumer)
	{
		for(var fluid : BuiltInRegistries.FLUID)
		{
			if(FertilizerPotency.getFor(fluid) != null)
			{
				consumer.accept(fluid);
			}
		}
	}
	
	@Override
	public void buildLayout(Fluid recipe, LayoutBuilder builder)
	{
		builder.inputSlot(15, 15).resource(FluidResource.of(recipe));
	}
	
	@Override
	public void buildWidgets(Fluid recipe, WidgetList widgets)
	{
		var fertilizerPotency = FertilizerPotency.getFor(recipe);
		var rate = EI.text().fluidFertilizersTime(fertilizerPotency.tickRate() / 20f);
		widgets.secondaryText(rate, 40, 14);
		var cost = EI.text().fluidFertilizersConsumes(fertilizerPotency.mbToConsumePerFertilizerTick());
		widgets.secondaryText(cost, 40, 24);
	}
}
