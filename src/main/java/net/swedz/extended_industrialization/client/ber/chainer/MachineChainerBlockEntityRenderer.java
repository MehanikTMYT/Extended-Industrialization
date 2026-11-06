package net.swedz.extended_industrialization.client.ber.chainer;

import aztech.modern_industrialization.client.util.ModelHelper;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelSet;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.client.model.chainer.MachineChainerBlockStateModel;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;

/**
 * Based on {@link aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer}
 */
public class MachineChainerBlockEntityRenderer implements BlockEntityRenderer<MachineChainerMachineBlockEntity, MachineChainerBlockEntityRenderer.ChainerRenderState>
{
	private final BlockModelSet blockModels;
	
	public MachineChainerBlockEntityRenderer(BlockEntityRendererProvider.Context context)
	{
		this.blockModels = Minecraft.getInstance().getModelManager().getBlockModelSet();
	}
	
	@Override
	public ChainerRenderState createRenderState()
	{
		return new ChainerRenderState();
	}
	
	@Override
	public void extractRenderState(MachineChainerMachineBlockEntity machine, ChainerRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
	{
		BlockEntityRenderer.super.extractRenderState(machine, state, partialTicks, cameraPosition, breakProgress);
		
		BlockState blockState = machine.getBlockState();
		var model = this.getMachineModel(blockState);
		
		var data = machine.getMachineModelData();
		state.activeQuads.clear();
		if(model != null && data.isActive())
		{
			for(var direction : Direction.values())
			{
				var quad = model.getActiveOverlayQuad(data, direction);
				if(quad != null)
				{
					int faceLight = LevelRenderer.getLightCoords(machine.getLevel(), machine.getBlockPos().relative(direction));
					state.activeQuads.add(new ActiveQuad(quad, faceLight));
				}
			}
		}
	}
	
	@Override
	public void submit(ChainerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
	{
		for(var activeQuad : state.activeQuads)
		{
			final var quad = activeQuad.quad;
			final int light = activeQuad.light;
			submitNodeCollector.submitCustomGeometry(poseStack, Sheets.cutoutBlockSheet(), (pose, buffer) ->
			{
				var instance = new QuadInstance();
				instance.setLightCoords(light);
				buffer.putBakedQuad(pose, quad, instance);
			});
		}
	}
	
	public record ActiveQuad(BakedQuad quad, int light)
	{
	}
	
	public static final class ChainerRenderState extends net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
	{
		public final java.util.List<ActiveQuad> activeQuads = new java.util.ArrayList<>();
	}
	
	private MachineChainerBlockStateModel getMachineModel(BlockState state)
	{
		var model = blockModels.get(state);
		if(model instanceof MachineChainerBlockStateModel chainerModel)
		{
			return chainerModel;
		}
		EI.LOGGER.warn("Model {} should have been a MachineChainerBlockStateModel, but was {}", state, model.getClass());
		return null;
	}
	
	@Override
	public int getViewDistance()
	{
		return 256;
	}
}
