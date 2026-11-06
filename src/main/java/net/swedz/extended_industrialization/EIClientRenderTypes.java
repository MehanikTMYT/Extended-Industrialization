package net.swedz.extended_industrialization;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderBuffersEvent;
import net.swedz.extended_industrialization.client.pipeline.EIRenderPipelines;
import org.joml.Matrix4f;

import java.util.function.BiFunction;
import java.util.function.Function;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class EIClientRenderTypes
{
	public static final BiFunction<Identifier, Boolean, RenderType> ARMOR_CUTOUT_NO_CULL_WITH_TRANSPARENCY = Util.memoize((texture, glow) -> armorCutoutWithTransparency(texture, glow, false));
	public static final BiFunction<Identifier, Boolean, RenderType> ARMOR_CUTOUT_CULL_WITH_TRANSPARENCY    = Util.memoize((texture, glow) -> armorCutoutWithTransparency(texture, glow, true));
	
	private static RenderType armorCutoutWithTransparency(Identifier texture, boolean glow, boolean cull)
	{
		RenderPipeline pipeline;
		if(glow)
		{
			pipeline = cull ? EIRenderPipelines.ARMOR_CUTOUT_GLOW_CULL : EIRenderPipelines.ARMOR_CUTOUT_GLOW;
		}
		else
		{
			return cull ? RenderTypes.entityCutoutCull(texture) : RenderTypes.armorCutoutNoCull(texture);
		}
		var state = RenderSetup.builder(pipeline)
				.withTexture("Sampler0", texture)
				.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
				.createRenderSetup();
		return RenderType.create("armor_cutout_%s_with_transparency".formatted(cull ? "cull" : "no_cull"), state);
	}
	
	public record NanoQuantumState(
			Identifier mask, boolean stars, float starScaleX, float starScaleY, boolean cull
	)
	{
	}
	
	public static final Function<NanoQuantumState, RenderType> NANO_QUANTUM = Util.memoize(EIClientRenderTypes::createNanoQuantum);
	
	private static RenderType createNanoQuantum(NanoQuantumState state)
	{
		RenderPipeline pipeline;
		if(state.stars())
		{
			if(state.starScaleX() == 24f && state.starScaleY() == 24f)
			{
				pipeline = state.cull() ? EIRenderPipelines.NANO_QUANTUM_ITEM : EIRenderPipelines.NANO_QUANTUM_ARMOR;
			}
			else
			{
				throw new IllegalArgumentException("Unsupported nano quantum star scale: %s, %s".formatted(state.starScaleX(), state.starScaleY()));
			}
		}
		else
		{
			pipeline = EIRenderPipelines.NANO_QUANTUM_DECORATION;
		}
		// RENDER_STARS is not used by the shader, it is only for readability of the pipeline
		var state2 = RenderSetup.builder(pipeline)
				.withTexture("Sampler0", state.mask())
				.withTexture("Sampler1", EI.id("textures/shaders/nano_quantum/glint.png"))
				.withTexture("Sampler2", EI.id("textures/shaders/nano_quantum/1.png"))
				.withTexture("Sampler3", EI.id("textures/shaders/nano_quantum/2.png"))
				.withTexture("Sampler4", EI.id("textures/shaders/nano_quantum/3.png"))
				.withTexture("Sampler5", EI.id("textures/shaders/nano_quantum/4.png"))
				.setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
				.createRenderSetup();
		return RenderType.create("nano_quantum_%s_%s".formatted(state.stars() ? "stars" : "no_stars", state.cull() ? "cull" : "no_cull"), state2);
	}
	
	public static final RenderType TESLA_ARC = RenderType.create(
			"tesla_arc",
			RenderSetup.builder(EIRenderPipelines.TESLA_ARC)
					.withTexture("Sampler0", EI.id("textures/vfx/tesla_arc.png"))
					.createRenderSetup()
	);
	
	public static final BiFunction<Float, Float, RenderType> TESLA_PLASMA = Util.memoize(EIClientRenderTypes::createTeslaPlasma);
	
	private static RenderType createTeslaPlasma(float scale, float speed)
	{
		// The scale and speed are packed into the texture transform matrix and unpacked by the shader
		var textureTransform = new net.minecraft.client.renderer.rendertype.TextureTransform(
				"tesla_plasma", () -> new Matrix4f().scale(scale, speed, 1f)
		);
		return RenderType.create(
				"plasma_%s_%s".formatted(scale, speed),
				RenderSetup.builder(EIRenderPipelines.TESLA_PLASMA)
						.withTexture("Sampler0", EI.id("textures/vfx/tesla_plasma.png"))
						.withTexture("Sampler1", net.minecraft.resources.Identifier.withDefaultNamespace("textures/atlas/blocks.png"))
						.setTextureTransform(textureTransform)
						.createRenderSetup()
		);
	}
	
	@SubscribeEvent
	private static void onRegisterRenderBuffers(RegisterRenderBuffersEvent event)
	{
		event.registerRenderBuffer(TESLA_ARC);
	}
}
