package net.swedz.extended_industrialization.client.model.tesla;

import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.UnbakedModelLoader;
import net.swedz.extended_industrialization.EI;

/**
 * This is basically an empty model but with a particle icon set to the tesla particle generator texture.
 */
public final class TeslaParticleGeneratorModel implements UnbakedModel
{
	public static final Identifier                      LOADER_ID = EI.id("tesla_particle_generator");
	public static final TeslaParticleGeneratorModel     INSTANCE  = new TeslaParticleGeneratorModel();
	public static final UnbakedModelLoader<TeslaParticleGeneratorModel> LOADER    = (json, ctx) -> INSTANCE;
	
	private static final TextureSlots.Data TEXTURES = TextureSlots.parseTextureMap(
			new com.google.gson.JsonParser()
					.parse("{\"particle\": \"extended_industrialization:block/tesla_particle_generator\"}")
					.getAsJsonObject()
	);
	
	private TeslaParticleGeneratorModel()
	{
	}
	
	@Override
	public TextureSlots.Data textureSlots()
	{
		return TEXTURES;
	}
	
	@Override
	public UnbakedGeometry geometry()
	{
		return (textureSlots, baker, modelState, name) -> QuadCollection.EMPTY;
	}
}
