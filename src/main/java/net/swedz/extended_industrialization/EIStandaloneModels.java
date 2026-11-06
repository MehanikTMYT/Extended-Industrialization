package net.swedz.extended_industrialization;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.List;

/**
 * Standalone (non-blockstate) models: tesla machine models (parsed for plasma/arc config)
 * and the nano saber sweep entity model.
 */
public final class EIStandaloneModels
{
	public static final StandaloneModelKey<BlockStateModelPart> LETHAL_TESLA_COIL = tesla("lethal_tesla_coil");
	public static final StandaloneModelKey<BlockStateModelPart> TESLA_COIL        = tesla("tesla_coil");
	public static final StandaloneModelKey<BlockStateModelPart> TESLA_HATCH       = tesla("tesla_hatch");
	public static final StandaloneModelKey<BlockStateModelPart> TESLA_RECEIVER    = tesla("tesla_receiver");
	public static final StandaloneModelKey<BlockStateModelPart> TESLA_TOWER       = tesla("tesla_tower");
	public static final List<StandaloneModelKey<BlockStateModelPart>> TESLA_PARTICLE_GENERATORS = List.of(
			tesla("tesla_particle_generator/small"),
			tesla("tesla_particle_generator/medium"),
			tesla("tesla_particle_generator/large"),
			tesla("tesla_particle_generator/extreme"),
			tesla("tesla_particle_generator/immense")
	);
	public static final StandaloneModelKey<BlockStateModelPart> NANO_SABER_SWEEP = tesla("entity/nano_saber_sweep");
	
	private static StandaloneModelKey<BlockStateModelPart> tesla(String path)
	{
		return new StandaloneModelKey<>(() -> EI.id(path).toString());
	}
	
	public static void register(net.neoforged.neoforge.client.event.ModelEvent.RegisterStandalone event)
	{
		register(event, LETHAL_TESLA_COIL, "tesla/lethal_tesla_coil");
		register(event, TESLA_COIL, "tesla/tesla_coil");
		register(event, TESLA_HATCH, "tesla/tesla_hatch");
		register(event, TESLA_RECEIVER, "tesla/tesla_receiver");
		register(event, TESLA_TOWER, "tesla/tesla_tower");
		for(var key : TESLA_PARTICLE_GENERATORS)
		{
			register(event, key, key.getName().replace("extended_industrialization:", ""));
		}
		register(event, NANO_SABER_SWEEP, "entity/nano_saber_sweep");
	}
	
	private static void register(net.neoforged.neoforge.client.event.ModelEvent.RegisterStandalone event, StandaloneModelKey<BlockStateModelPart> key, String modelPath)
	{
		event.register(key, SimpleUnbakedStandaloneModel.simpleModelWrapper(EI.id(modelPath)));
	}
}
