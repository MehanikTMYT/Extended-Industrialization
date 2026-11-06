package net.swedz.extended_industrialization.client.ber.tesla;

import net.minecraft.util.context.ContextKey;
import net.swedz.extended_industrialization.EIClient;
import net.swedz.extended_industrialization.EIClientRenderTypes;
import aztech.modern_industrialization.MITags;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.Minecraft;
import aztech.modern_industrialization.client.util.RenderHelper;
import net.minecraft.util.ARGB;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcPoint;
import net.swedz.extended_industrialization.client.ber.tesla.arc.TeslaArcRenderer;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaArcInstance;
import net.swedz.extended_industrialization.client.ber.tesla.behavior.TeslaBehavior;
import net.swedz.extended_industrialization.client.model.tesla.TeslaBakedModel;
import net.swedz.extended_industrialization.client.model.tesla.TeslaClientModels;
import net.swedz.extended_industrialization.machines.blockentity.tesla.TeslaParticleGeneratorMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetworkPart;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class TeslaPartRenderer
{
	private TeslaPartRenderer()
	{
	}
	
	// ---------- Held network key / wrench helpers ----------
	
	public static Optional<net.swedz.tesseract.neoforge.api.WorldPos> getHeldNetworkKey()
	{
		var player = Minecraft.getInstance().player;
		if(player == null)
		{
			return Optional.empty();
		}
		return player.getMainHandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getMainHandItem().get(EIComponents.SELECTED_TESLA_NETWORK).key()) :
				(player.getOffhandItem().has(EIComponents.SELECTED_TESLA_NETWORK) ? Optional.of(player.getOffhandItem().get(EIComponents.SELECTED_TESLA_NETWORK).key()) : Optional.empty());
	}
	
	public static boolean isHoldingWrench()
	{
		var player = Minecraft.getInstance().player;
		if(player == null)
		{
			return false;
		}
		return player.getMainHandItem().is(MITags.WRENCHES) ||
			   player.getOffhandItem().is(MITags.WRENCHES);
	}
	
	// ---------- Linked network highlight (level renderer pass) ----------
	
	private static final Set<BlockPos> LINKED_HIGHLIGHTS = Sets.newConcurrentHashSet();
	private static final Set<BlockPos> WRENCH_HIGHLIGHTS = Sets.newConcurrentHashSet();
	private static final ContextKey<Set<BlockPos>> LINKED_HIGHLIGHT_KEY = new ContextKey<>(EI.id("tesla_linked_highlights"));
	private static final ContextKey<Set<BlockPos>> WRENCH_HIGHLIGHT_KEY = new ContextKey<>(EI.id("tesla_wrench_highlights"));
	
	public static void extractHighlight(MachineBlockEntity machine)
	{
		var player = Minecraft.getInstance().player;
		var pos = machine.getBlockPos();
		
		if(machine instanceof TeslaNetworkPart part)
		{
			getHeldNetworkKey().ifPresent((networkKey) ->
			{
				if(part.hasNetwork() &&
				   part.getNetworkKey().equals(networkKey) &&
				   player.distanceToSqr(pos.getCenter()) <= Mth.square(EIClient.config().teslaLinkedOverlayRenderDistance()))
				{
					LINKED_HIGHLIGHTS.add(pos);
				}
			});
		}
		else if(machine instanceof TeslaParticleGeneratorMachineBlockEntity)
		{
			if(isHoldingWrench())
			{
				WRENCH_HIGHLIGHTS.add(pos);
			}
		}
	}
	
	@SubscribeEvent
	private static void onSubmitCustomGeometry(SubmitCustomGeometryEvent event)
	{
		var camera = event.getLevelRenderState().cameraRenderState.pos;
		var pose = event.getPoseStack();
		var collector = event.getSubmitNodeCollector();
		try
		{
			for(var pos : LINKED_HIGHLIGHTS)
			{
				pose.pushPose();
				pose.translate(pos.getX() - camera.x - 0.005, pos.getY() - camera.y - 0.005, pos.getZ() - camera.z - 0.005);
				pose.scale(1.01f, 1.01f, 1.01f);
				RenderHelper.drawCube(pose, collector, 0xFF6F6FFF);
				pose.popPose();
			}
			for(var pos : WRENCH_HIGHLIGHTS)
			{
				pose.pushPose();
				pose.translate(pos.getX() - camera.x - 0.005, pos.getY() - camera.y - 0.005, pos.getZ() - camera.z - 0.005);
				pose.scale(1.01f, 1.01f, 1.01f);
				RenderHelper.drawCube(pose, collector, 0xFFFFFFFF);
				pose.popPose();
			}
		}
		finally
		{
			LINKED_HIGHLIGHTS.clear();
			WRENCH_HIGHLIGHTS.clear();
		}
	}
	
	// ---------- Arc instances ----------
	
	private static final java.util.Map<BlockPos, TeslaArcInstance> TESLA_ARCS = Maps.newConcurrentMap();
	
	public static void onLevelUnload()
	{
		TESLA_ARCS.clear();
	}
	
	public static void removeArcInstance(BlockPos pos)
	{
		TESLA_ARCS.remove(pos);
	}
	
	public static TeslaArcInstance getArcInstance(BlockPos pos)
	{
		var level = Minecraft.getInstance().level;
		if(level.getBlockEntity(pos) instanceof MachineBlockEntity machine &&
		   machine instanceof TeslaBehavior behavior)
		{
			var tesla = getTeslaModel(behavior.getTeslaModelLocation());
			if(tesla != null && tesla.arcs() != null)
			{
				return TESLA_ARCS.computeIfAbsent(
						pos,
						(__) -> new TeslaArcInstance(
								pos,
								() -> machine.orientation.facingDirection,
								() -> getTeslaModel(behavior.getTeslaModelLocation())
						)
				);
			}
		}
		return null;
	}
	
	public static TeslaBakedModel getTeslaModel(Identifier location)
	{
		return TeslaClientModels.get(location);
	}
	
	// ---------- BER extract/submit hooks ----------
	
	public record ExtractedArc(List<TeslaArcPoint> points, float alpha)
	{
	}
	
	public static void extract(MachineBlockEntity machine, boolean shouldTeslaRender, Vector3f color, float partialTick, List<ExtractedArc> arcsOut, boolean plasmaOut)
	{
		if(!EIClient.config().renderTeslaAnimations() || !(machine instanceof TeslaBehavior behavior))
		{
			return;
		}
		int renderDistance = EIClient.config().teslaAnimationsRenderDistance();
		if(renderDistance != 0 && !Minecraft.getInstance().player.position().closerThan(machine.getBlockPos().getCenter(), renderDistance))
		{
			return;
		}
		var tesla = getTeslaModel(behavior.getTeslaModelLocation());
		if(tesla == null)
		{
			return;
		}
		var arcs = tesla.arcs();
		if(arcs != null && shouldTeslaRender)
		{
			var arcInstance = getArcInstance(machine.getBlockPos());
			if(arcInstance != null)
			{
				for(var trail : arcInstance.getTrails())
				{
					List<TeslaArcPoint> points = trail.points();
					if(points.size() < 2)
					{
						continue;
					}
					boolean instant = arcs.duration() == 0;
					float alpha = 0.9f;
					if(!instant)
					{
						int ticks = points.getFirst().timeActive();
						int halfPoints = points.size() / 2;
						if(ticks == 0 || ticks == 1)
						{
							points = points.subList(0, (int) (halfPoints * partialTick) + (ticks == 1 ? halfPoints : 0));
						}
						alpha *= (ticks == 0 ? partialTick : (ticks == arcs.duration() ? (1 - partialTick) : 1));
					}
					arcsOut.add(new ExtractedArc(List.copyOf(points), alpha));
				}
			}
		}
	}
	
	public static void submit(MachineBlockEntity machine, boolean shouldTeslaRender, Vector3f color, float partialTick,
							  com.mojang.blaze3d.vertex.PoseStack matrices, SubmitNodeCollector collector, CameraRenderState camera,
							  List<ExtractedArc> arcs, boolean renderPlasma)
	{
		var tesla = getTeslaModel(((TeslaBehavior) machine).getTeslaModelLocation());
		if(tesla == null)
		{
			return;
		}
		var modelArcs = tesla.arcs();
		
		// Arcs
		if(modelArcs != null && shouldTeslaRender)
		{
			for(var extracted : arcs)
			{
				final var points = extracted.points();
				final float alpha = extracted.alpha();
				collector.submitCustomGeometry(matrices, EIClientRenderTypes.TESLA_ARC, (pose, buffer) ->
						TeslaArcRenderer.renderArcPose(pose.pose(), buffer, points, (i) -> (1 - i) * modelArcs.widthScale(),
								color.x(), color.y(), color.z(), alpha)
				);
			}
		}
		
		// Plasma
		var plasma = tesla.plasma();
		if(plasma != null && shouldTeslaRender && renderPlasma)
		{
			matrices.pushPose();
			
			var worldPosition = machine.getBlockPos().getCenter();
			var worldOffset = plasma.worldOffset(worldPosition, machine.orientation.facingDirection);
			var offset = worldOffset.subtract(worldPosition).add(0.5, 0.5, 0.5);
			matrices.translate(offset.x(), offset.y(), offset.z());
			
			float modelScale = plasma.scale();
			matrices.scale(modelScale, modelScale, modelScale);
			
			float textureScale = plasma.textureScale();
			float speed = plasma.speed();
			var quads = tesla.quads();
			var plasmaColor = ARGB.color((int) (0.8f * 255), (int) (color.x() * 255), (int) (color.y() * 255), (int) (color.z() * 255));
			collector.submitCustomGeometry(matrices, EIClientRenderTypes.TESLA_PLASMA.apply(textureScale, speed), (pose, buffer) ->
			{
				var instance = new QuadInstance();
				instance.setColor(plasmaColor);
				for(var quad : quads.getAll())
				{
					buffer.putBakedQuad(pose, quad, instance);
				}
			});
			
			matrices.popPose();
		}
	}
	
}
