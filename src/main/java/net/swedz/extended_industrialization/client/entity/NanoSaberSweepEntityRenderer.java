package net.swedz.extended_industrialization.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIClientSheets;
import net.swedz.extended_industrialization.EIStandaloneModels;
import net.swedz.extended_industrialization.component.RainbowDataComponent;
import net.swedz.extended_industrialization.entity.NanoSaberSweepEntity;

import java.util.ArrayList;
import java.util.List;

public final class NanoSaberSweepEntityRenderer extends EntityRenderer<NanoSaberSweepEntity, NanoSaberSweepEntityRenderer.RenderState>
{
	public static final class RenderState extends EntityRenderState
	{
		public int color;
		public boolean rainbow;
		public double mx, my, mz;
		public final List<BakedQuad> quads = new ArrayList<>();
	}
	
	public NanoSaberSweepEntityRenderer(EntityRendererProvider.Context context)
	{
		super(context);
	}
	
	@Override
	public RenderState createRenderState()
	{
		return new RenderState();
	}
	
	@Override
	public void extractRenderState(NanoSaberSweepEntity entity, RenderState state, float partialTicks)
	{
		super.extractRenderState(entity, state, partialTicks);
		
		state.color = entity.isRainbow() ? RainbowDataComponent.getCurrentRainbowColor() : (entity.getColor() | 0xFF000000);
		state.rainbow = entity.isRainbow();
		var movement = entity.getDeltaMovement();
		state.mx = movement.x();
		state.my = movement.y();
		state.mz = movement.z();
		
		state.quads.clear();
		var model = Minecraft.getInstance().getModelManager().getStandaloneModel(EIStandaloneModels.NANO_SABER_SWEEP);
		if(model != null)
		{
			List<BlockStateModelPart> parts = new ArrayList<>();
			model.collectParts(RandomSource.create(42), parts);
			for(var part : parts)
			{
				state.quads.addAll(part.getQuads(null));
				for(var direction : net.minecraft.core.Direction.values())
				{
					state.quads.addAll(part.getQuads(direction));
				}
			}
		}
	}
	
	@Override
	public void submit(RenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState camera)
	{
		if(state.quads.isEmpty())
		{
			return;
		}
		
		matrices.pushPose();
		
		var movement = new net.minecraft.world.phys.Vec3(state.mx, state.my, state.mz);
		if(movement.lengthSqr() > 1.0E-8)
		{
			movement = movement.normalize();
			float yaw = (float) Math.atan2(movement.x(), movement.z());
			float pitch = (float) Math.asin(-movement.y());
			matrices.mulPose(Axis.YP.rotation(yaw));
			matrices.mulPose(Axis.XP.rotation(pitch));
			matrices.mulPose(Axis.YP.rotationDegrees(180));
		}
		
		int color = state.color;
		var instance = new QuadInstance();
		instance.setColor(color);
		instance.setLightCoords(LightTexture.FULL_BRIGHT);
		var renderType = RenderTypes.entityCutout(EIClientSheets.NANO_SABER_SWEEP);
		for(var quad : state.quads)
		{
			collector.submitCustomGeometry(matrices, renderType, (pose, buffer) -> buffer.putBakedQuad(pose, quad, instance));
		}
		
		matrices.popPose();
	}
}
