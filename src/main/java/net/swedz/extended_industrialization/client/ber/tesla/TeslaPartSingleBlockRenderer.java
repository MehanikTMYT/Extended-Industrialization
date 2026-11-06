package net.swedz.extended_industrialization.client.ber.tesla;

import aztech.modern_industrialization.client.machines.MachineBlockEntityRenderer;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

public class TeslaPartSingleBlockRenderer extends MachineBlockEntityRenderer<MachineBlockEntity>
{
	public TeslaPartSingleBlockRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(ctx);
	}
	
	@Override
	public MachineRenderState createRenderState()
	{
		return new TeslaRenderState();
	}
	
	@Override
	public void extractRenderState(MachineBlockEntity machine, MachineRenderState machineState, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
	{
		super.extractRenderState(machine, machineState, partialTicks, cameraPosition, breakProgress);
		var state = (TeslaRenderState) machineState;
		state.machine = machine;
		state.arcs.clear();
		state.shouldTeslaRender = false;
		state.hasPlasma = false;
		TeslaPartRenderer.extractHighlight(machine);
		if(machine instanceof TeslaBehavior behavior)
		{
			state.teslaColor.set(behavior.getTeslaColor());
			state.shouldTeslaRender = behavior.shouldTeslaRender();
			TeslaPartRenderer.extract(machine, behavior.shouldTeslaRender(), state.teslaColor, partialTicks, state.arcs, true);
			state.hasPlasma = true;
		}
	}
	
	@Override
	public void submit(MachineRenderState machineState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
	{
		super.submit(machineState, poseStack, submitNodeCollector, camera);
		var state = (TeslaRenderState) machineState;
		if(state.machine != null)
		{
			TeslaPartRenderer.submit(state.machine, state.shouldTeslaRender, state.teslaColor, 0, poseStack, submitNodeCollector, camera, state.arcs, true);
		}
	}
}
