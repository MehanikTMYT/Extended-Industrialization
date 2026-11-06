package net.swedz.extended_industrialization.datagen.client;

import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.datagen.client.provider.LanguageDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.models.BlockModelsDatagenProvider;
import net.swedz.extended_industrialization.datagen.client.provider.models.ItemModelsDatagenProvider;
import net.swedz.tesseract.neoforge.datagen.mi.MIDatagenHooks;

import java.util.function.Function;

public final class DatagenDelegatorClient
{
	public static void configure(GatherDataEvent.Client event)
	{
		MIDatagenHooks.Client.includeMISprites(event);
		
		MIDatagenHooks.Client.addTexturesHook(event, EI.ID, EIFluids.values());
		MIDatagenHooks.Client.addMachineCasingModelsHook(event, EI.ID);
		
		add(event, BlockModelsDatagenProvider::new);
		add(event, ItemModelsDatagenProvider::new);
		add(event, LanguageDatagenProvider::new);
	}
	
	private static void add(GatherDataEvent.Client event, Function<GatherDataEvent.Client, DataProvider> providerCreator)
	{
		event.getGenerator().addProvider(true, providerCreator.apply(event));
	}
}
