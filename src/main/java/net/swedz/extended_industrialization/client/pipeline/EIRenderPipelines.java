package net.swedz.extended_industrialization.client.pipeline;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.swedz.extended_industrialization.EI;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class EIRenderPipelines
{
	public static final RenderPipeline ARMOR_CUTOUT_GLOW = register(
			RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
					.withLocation(EI.id("pipeline/armor_cutout_glow"))
					.withVertexShader(EI.id("core/armor_cutout_glow"))
					.withFragmentShader(EI.id("core/armor_cutout_glow"))
					.withSampler("Sampler0")
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
					.withCull(false)
					.build()
	);
	
	public static final RenderPipeline ARMOR_CUTOUT_GLOW_CULL = register(
			RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
					.withLocation(EI.id("pipeline/armor_cutout_glow_cull"))
					.withVertexShader(EI.id("core/armor_cutout_glow"))
					.withFragmentShader(EI.id("core/armor_cutout_glow"))
					.withSampler("Sampler0")
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
					.withCull(true)
					.build()
	);
	
	public static final RenderPipeline NANO_QUANTUM_ITEM = register(
			nanoQuantum("item", 24f, 24f, true)
	);
	
	public static final RenderPipeline NANO_QUANTUM_ARMOR = register(
			nanoQuantum("armor", 1f, 0.5f, false)
	);
	
	public static final RenderPipeline NANO_QUANTUM_DECORATION = register(
			nanoQuantum("decoration", 1f, 0.5f, true)
	);
	
	private static RenderPipeline nanoQuantum(String variant, float starScaleX, float starScaleY, boolean cull)
	{
		var builder = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
				.withLocation(EI.id("pipeline/nano_quantum_%s".formatted(variant)))
				.withVertexShader(EI.id("core/nano_quantum"))
				.withFragmentShader(EI.id("core/nano_quantum"))
				.withSampler("Sampler0")
				.withSampler("Sampler1")
				.withSampler("Sampler2")
				.withSampler("Sampler3")
				.withSampler("Sampler4")
				.withSampler("Sampler5")
				.withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
				.withCull(cull);
		if(variant.equals("item") || variant.equals("armor"))
		{
			builder = builder
					.withShaderDefine("RENDER_STARS")
					.withShaderDefine("STAR_SCALE_X", starScaleX)
					.withShaderDefine("STAR_SCALE_Y", starScaleY);
		}
		return builder.build();
	}
	
	public static final RenderPipeline TESLA_ARC = register(
			RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
					.withLocation(EI.id("pipeline/tesla_arc"))
					.withVertexShader(EI.id("core/tesla_arc"))
					.withFragmentShader(EI.id("core/tesla_arc"))
					.withSampler("Sampler0")
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
					.withCull(true)
					.build()
	);
	
	public static final RenderPipeline TESLA_PLASMA = register(
			RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
					.withLocation(EI.id("pipeline/tesla_plasma"))
					.withVertexShader(EI.id("core/tesla_plasma"))
					.withFragmentShader(EI.id("core/tesla_plasma"))
					.withSampler("Sampler0")
					.withSampler("Sampler1")
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					.withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
					.withCull(true)
					.build()
	);
	
	private static RenderPipeline register(RenderPipeline pipeline)
	{
		return pipeline;
	}
	
	@SubscribeEvent
	private static void registerPipelines(RegisterRenderPipelinesEvent event)
	{
		event.registerPipeline(ARMOR_CUTOUT_GLOW);
		event.registerPipeline(ARMOR_CUTOUT_GLOW_CULL);
		event.registerPipeline(NANO_QUANTUM_ITEM);
		event.registerPipeline(NANO_QUANTUM_ARMOR);
		event.registerPipeline(NANO_QUANTUM_DECORATION);
		event.registerPipeline(TESLA_ARC);
		event.registerPipeline(TESLA_PLASMA);
	}
}
