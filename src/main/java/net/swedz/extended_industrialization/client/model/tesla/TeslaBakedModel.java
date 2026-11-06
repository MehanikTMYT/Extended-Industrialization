package net.swedz.extended_industrialization.client.model.tesla;

import net.minecraft.client.resources.model.geometry.QuadCollection;

/**
 * The baked tesla model data: the plasma quads plus the parsed plasma/arc configuration.
 * Registered into {@link TeslaClientModels} when the model is baked.
 */
public record TeslaBakedModel(QuadCollection quads, TeslaUnbakedModel.Plasma plasma, TeslaUnbakedModel.Arcs arcs)
{
	public boolean hasPlasma()
	{
		return plasma != null;
	}
	
	public boolean hasArcs()
	{
		return arcs != null;
	}
}
