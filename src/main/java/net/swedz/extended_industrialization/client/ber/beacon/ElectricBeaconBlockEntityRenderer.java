package net.swedz.extended_industrialization.client.ber.beacon;

import aztech.modern_industrialization.client.machines.MachineRenderState;
import aztech.modern_industrialization.client.machines.multiblocks.MultiblockMachineBER;
import aztech.modern_industrialization.machines.multiblocks.MultiblockMachineBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.machines.blockentity.multiblock.beacon.ElectricBeaconMachineBlockEntity;

import java.util.ArrayList;
import java.util.List;

public final class ElectricBeaconBlockEntityRenderer extends MultiblockMachineBER
{
	public ElectricBeaconBlockEntityRenderer(BlockEntityRendererProvider.Context context)
	{
		super(context);
	}
	
	private static final class BeaconRenderState extends MachineRenderState
	{
		final List<BeaconRenderer.BeaconRenderState.Section> sections = new ArrayList<>();
		float animationTime;
		boolean active;
	}
	
	@Override
	public MachineRenderState createRenderState()
	{
		return new BeaconRenderState();
	}
	
	@Override
	public void extractRenderState(MultiblockMachineBlockEntity machine, MachineRenderState machineState, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
	{
		super.extractRenderState(machine, machineState, partialTicks, cameraPosition, breakProgress);
		if(!(machine instanceof ElectricBeaconMachineBlockEntity beacon))
		{
			throw new UnsupportedOperationException("ElectricBeaconBlockEntityRenderer requires block entity type of ElectricBeaconMachineBlockEntity");
		}
		var state = (BeaconRenderState) machineState;
		state.active = beacon.isActive();
		state.sections.clear();
		if(state.active && beacon.getLevel() != null)
		{
			state.animationTime = Math.floorMod(beacon.getLevel().getGameTime(), 40) + partialTicks;
			for(var section : beacon.getBeamSections())
			{
				state.sections.add(new BeaconRenderer.BeaconRenderState.Section(section.color(), section.height()));
			}
		}
	}
	
	@Override
	public void submit(MachineRenderState machineState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera)
	{
		super.submit(machineState, poseStack, submitNodeCollector, camera);
		var state = (BeaconRenderState) machineState;
		if(!state.active)
		{
			return;
		}
		int beamStart = 0;
		for(int index = 0; index < state.sections.size(); index++)
		{
			var section = state.sections.get(index);
			BeaconRenderer.submitBeaconBeam(
					poseStack,
					submitNodeCollector,
					BeaconRenderer.BEAM_LOCATION,
					1,
					state.animationTime,
					beamStart,
					index == state.sections.size() - 1 ? 1024 : section.height(),
					section.color(),
					0.2f,
					0.25f
			);
			beamStart += section.height();
		}
	}
}
