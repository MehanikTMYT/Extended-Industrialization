package net.swedz.extended_industrialization.datagen.server;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.datagen.server.provider.advancements.AdvancementDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.datamaps.DataMapDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.loottable.BlockLootTableDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.loottable.LootModifierDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.AlloySmelterRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.BendingMachineRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.CanningMachineRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.CommonRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.ComposterRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.MachineItemRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.MaterialRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.NPKProcessingRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.recipes.VanillaCompatRecipesServerDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.tags.BlockTagDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.tags.DamageTypeTagDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.tags.FluidTagDatagenProvider;
import net.swedz.extended_industrialization.datagen.server.provider.tags.ItemTagDatagenProvider;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public final class DatagenDelegatorServer
{
	public static void configure(GatherDataEvent.Client event)
	{
		add(event, AdvancementDatagenProvider::new);
		
		add(event, DataMapDatagenProvider::new);
		
		addLootTable(event, BlockLootTableDatagenProvider::new);
		add(event, LootModifierDatagenProvider::new);
		
		
		        event.getGenerator().addProvider(true, new net.minecraft.data.recipes.RecipeProvider.Runner(event.getGenerator().getPackOutput(), event.getLookupProvider()) {
            @Override protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, net.minecraft.data.recipes.RecipeOutput output) {
                return new net.minecraft.data.recipes.RecipeProvider(registries, output) {
                    @Override protected void buildRecipes() {
                        new AlloySmelterRecipesServerDatagenProvider(registries, output).generate();
                        new BendingMachineRecipesServerDatagenProvider(registries, output).generate();
                        new CanningMachineRecipesServerDatagenProvider(registries, output).generate();
                        new CommonRecipesServerDatagenProvider(registries, output).generate();
                        new ComposterRecipesServerDatagenProvider(registries, output).generate();
                        new MachineItemRecipesServerDatagenProvider(registries, output).generate();
                        new MaterialRecipesServerDatagenProvider(registries, output).generate();
                        new NPKProcessingRecipesServerDatagenProvider(registries, output).generate();
                        new VanillaCompatRecipesServerDatagenProvider(registries, output).generate();
                    }
                };
            }
            @Override public String getName() { return "Extended Industrialization recipes"; }
        });

		add(event, BlockTagDatagenProvider::new);
		add(event, DamageTypeTagDatagenProvider::new);
		add(event, FluidTagDatagenProvider::new);
		add(event, ItemTagDatagenProvider::new);
	}
	
	private static void add(GatherDataEvent.Client event, Function<GatherDataEvent.Client, DataProvider> providerCreator)
	{
		event.getGenerator().addProvider(true, providerCreator.apply(event));
	}
	
	private static void addLootTable(GatherDataEvent.Client event, Function<HolderLookup.Provider, LootTableSubProvider> providerCreator)
	{
		event.getGenerator().addProvider(
				true,
				new LootTableProvider(
						event.getGenerator().getPackOutput(),
						Set.of(),
						List.of(new LootTableProvider.SubProviderEntry(providerCreator, LootContextParamSets.BLOCK)),
						event.getLookupProvider()
				)
		);
	}
}
