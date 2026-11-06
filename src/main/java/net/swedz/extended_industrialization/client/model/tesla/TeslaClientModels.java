package net.swedz.extended_industrialization.client.model.tesla;

import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry of baked tesla models by their model id. Filled by {@link TeslaUnbakedModel} during model baking.
 */
public final class TeslaClientModels
{
	private static final Map<Identifier, TeslaBakedModel> MODELS = new ConcurrentHashMap<>();
	
	private TeslaClientModels()
	{
	}
	
	public static void register(Identifier id, TeslaBakedModel model)
	{
		MODELS.put(id, model);
	}
	
	public static TeslaBakedModel get(Identifier id)
	{
		return MODELS.get(id);
	}
}
