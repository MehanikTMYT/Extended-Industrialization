package net.swedz.extended_industrialization.client.ber.chainer;

import aztech.modern_industrialization.client.util.RenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerComponent;

import java.util.ArrayList;
import java.util.List;

public final class MachineChainerHighlightRenderer extends MachineChainerBlockEntityRenderer
{
	private static final int COLOR_SUCCESS = 0xFF6FFF6F;
	private static final int COLOR_FAILURE = 0xFFFF6F6F;
	
	private static final String ARROW_LEFT = "\u2190";
	private static final String ARROW_UP = "\u2191";
	private static final String ARROW_RIGHT = "\u2192";
	private static final String ARROW_DOWN = "\u2193";
	
	public MachineChainerHighlightRenderer(BlockEntityRendererProvider.Context ctx)
	{
		super(ctx);
	}
	
	private static final class HighlightRenderState extends ChainerRenderState
	{
		final List<Highlight> highlights = new ArrayList<>();
		final List<NumberLabel> labels = new ArrayList<>();
	}
	
	private record Highlight(BlockPos offset, float red, float green, float blue)
	{
	}
	
	private record NumberLabel(BlockPos offset, Direction renderDirection, Direction playerDirection, String text, int color, String arrow)
	{
	}
	
	@Override
	public ChainerRenderState createRenderState()
	{
		return new HighlightRenderState();
	}
	
	@Override
	public boolean shouldRenderOffScreen()
	{
		return true;
	}
	
	@Override
	public net.minecraft.world.phys.AABB getRenderBoundingBox(MachineChainerMachineBlockEntity blockEntity)
	{
		var links = blockEntity.getChainerComponent().links();
		boolean hasConnections = links.hasConnections();
		boolean hasFailure = links.failPosition().isPresent();
		if(hasConnections || hasFailure)
		{
			var endPos = hasFailure ?
					links.failPosition().get() :
					links.position(links.count() + 1);
			return new AABB(
					Vec3.atLowerCornerOf(blockEntity.getBlockPos()),
					Vec3.atLowerCornerOf(endPos).add(1, 1, 1)
			);
		}
		else
		{
			return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
		}
	}
	
	@Override
	public void extractRenderState(MachineChainerMachineBlockEntity machine, ChainerRenderState machineState, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress)
	{
		super.extractRenderState(machine, machineState, partialTicks, cameraPosition, breakProgress);
		var state = (HighlightRenderState) machineState;
		state.highlights.clear();
		state.labels.clear();
		
		var originPos = machine.getBlockPos();
		var links = machine.getChainerComponent().links();
		
		boolean holdingWrench = isHoldingWrench();
		boolean holdingMachine = isHoldingMachine(machine);
		
		if(holdingWrench || holdingMachine)
		{
			if(links.hasFailure())
			{
				if(holdingWrench)
				{
					state.highlights.add(new Highlight(
							links.failPosition().orElseThrow().subtract(originPos),
							1f, 111f / 256, 111f / 256
					));
				}
			}
			else if(links.maxConnections() > links.count())
			{
				if(holdingMachine)
				{
					state.highlights.add(new Highlight(
							links.positionAfter().subtract(originPos),
							111f / 256, 1f, 111f / 256
					));
				}
			}
		}
		
		if(holdingWrench)
		{
			int count = links.count();
			int color = COLOR_SUCCESS;
			if(links.hasFailure())
			{
				count = links.failPositionOffset();
				color = COLOR_FAILURE;
			}
			
			if(count > 0)
			{
				var playerDirection = Direction.fromYRot(Minecraft.getInstance().player.yHeadRot);
				var renderDirection = this.pickNumberRenderFace(machine);
				var arrow = this.pickArrowSymbol(machine, playerDirection, renderDirection);
				
				for(int i = 1; i <= count; i++)
				{
					var pos = links.position(i);
					state.labels.add(new NumberLabel(
							pos.subtract(originPos),
							renderDirection,
							playerDirection,
							Integer.toString(i),
							color,
							arrow
					));
				}
			}
		}
	}
	
	@Override
	public void submit(ChainerRenderState machineState, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera)
	{
		super.submit(machineState, poseStack, collector, camera);
		var state = (HighlightRenderState) machineState;
		
		for(var highlight : state.highlights)
		{
			poseStack.pushPose();
			var offset = highlight.offset();
			poseStack.translate(offset.getX() - 0.005, offset.getY() - 0.005, offset.getZ() - 0.005);
			poseStack.scale(1.01f, 1.01f, 1.01f);
			// The cube is tinted by averaging the rgb channels into one color
			int color = (int) (highlight.red() * 255) << 16 | (int) (highlight.green() * 255) << 8 | (int) (highlight.blue() * 255);
			RenderHelper.drawCube(poseStack, collector, 0xFF000000 | color);
			poseStack.popPose();
		}
		
		for(var label : state.labels)
		{
			var offset = label.offset();
			var center = Vec3.atCenterOf(offset);
			
			poseStack.pushPose();
			
			float stepAmount = 0.52f;
			poseStack.translate(
					center.x() + (label.renderDirection().getStepX() * stepAmount),
					center.y() + (label.renderDirection().getStepY() * stepAmount),
					center.z() + (label.renderDirection().getStepZ() * stepAmount)
			);
			poseStack.scale(0.03f, 0.03f, 0.03f);
			poseStack.mulPose(label.playerDirection().getOpposite().getRotation());
			poseStack.mulPose(label.renderDirection().getRotation());
			if(label.renderDirection() == Direction.NORTH)
			{
				poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180));
			}
			else if(label.renderDirection() == Direction.WEST)
			{
				poseStack.mulPose(com.mojang.math.Axis.ZN.rotationDegrees(90));
			}
			else if(label.renderDirection() == Direction.EAST)
			{
				poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90));
			}
			
			this.renderCenteredText(label.text(), label.color(), 0, 0, poseStack, collector);
			
			float arrowTextXOffset = 10;
			float arrowTextYOffset = 10;
			switch (label.arrow())
			{
				case ARROW_LEFT -> this.renderCenteredText(label.arrow(), label.color(), -arrowTextXOffset, 0, poseStack, collector);
				case ARROW_UP -> this.renderCenteredText(label.arrow(), label.color(), 0, -arrowTextYOffset, poseStack, collector);
				case ARROW_RIGHT -> this.renderCenteredText(label.arrow(), label.color(), arrowTextXOffset, 0, poseStack, collector);
				case ARROW_DOWN -> this.renderCenteredText(label.arrow(), label.color(), 0, arrowTextYOffset, poseStack, collector);
			}
			
			poseStack.popPose();
		}
	}
	
	private void renderCenteredText(String text, int color, float x, float y,
									PoseStack matrices, SubmitNodeCollector collector)
	{
		Font font = Minecraft.getInstance().font;
		float textX = font.width(text) / 2f;
		float textY = font.lineHeight / 2f;
		
		FormattedCharSequence sequence = Component.literal(text).getVisualOrderText();
		collector.submitText(
				matrices, -textX + x + 1, -textY + y + 1,
				sequence, false,
				Font.DisplayMode.NORMAL, RenderHelper.FULL_LIGHT,
				0x000000, 0x000000, 0
		);
		collector.submitText(
				matrices, -textX + x, -textY + y,
				sequence, false,
				Font.DisplayMode.NORMAL, RenderHelper.FULL_LIGHT,
				color, 0x000000, 0
		);
	}
	
	private Direction pickNumberRenderFace(MachineChainerMachineBlockEntity machine)
	{
		int playerY = (int) Math.round(Minecraft.getInstance().player.getY());
		
		var machineDirection = machine.orientation.facingDirection;
		if(machineDirection.getAxis().isHorizontal())
		{
			int machineY = machine.getBlockPos().getY();
			
			if(playerY == machineY || playerY == machineY - 1)
			{
				return Direction.fromYRot(Minecraft.getInstance().player.yHeadRot).getOpposite();
			}
			else if(playerY < machineY)
			{
				return Direction.DOWN;
			}
			else
			{
				return Direction.UP;
			}
		}
		else
		{
			int machineEndY = machine.getChainerComponent().links().positionAfter().getY();
			
			if(machineDirection == Direction.UP && playerY >= machineEndY)
			{
				return Direction.UP;
			}
			else if(machineDirection == Direction.DOWN && playerY < machineEndY)
			{
				return Direction.DOWN;
			}
			
			return Direction.fromYRot(Minecraft.getInstance().player.yHeadRot).getOpposite();
		}
	}
	
	private String pickArrowSymbol(MachineChainerMachineBlockEntity machine,
								   Direction playerDirection, Direction renderDirection)
	{
		var links = machine.getChainerComponent().links();
		var machineDirection = links.direction();
		
		var playerDirectionLeft = playerDirection.getCounterClockWise();
		var playerDirectionRight = playerDirection.getClockWise();
		
		var arrow = "";
		if(renderDirection != machineDirection && renderDirection != machineDirection.getOpposite())
		{
			if(playerDirection == machineDirection)
			{
				arrow = renderDirection == Direction.DOWN ? ARROW_DOWN : ARROW_UP;
			}
			else if(playerDirection == machineDirection.getOpposite())
			{
				arrow = renderDirection == Direction.DOWN ? ARROW_UP : ARROW_DOWN;
			}
			else if(playerDirectionLeft == machineDirection)
			{
				arrow = ARROW_LEFT;
			}
			else if(playerDirectionRight == machineDirection)
			{
				arrow = ARROW_RIGHT;
			}
			else if(machineDirection == Direction.UP)
			{
				arrow = ARROW_UP;
			}
			else if(machineDirection == Direction.DOWN)
			{
				arrow = ARROW_DOWN;
			}
		}
		return arrow;
	}
	
	private static boolean isHoldingMachine(MachineChainerMachineBlockEntity machine)
	{
		var player = Minecraft.getInstance().player;
		return machine.getChainerComponent().links().test(player.getMainHandItem()).isSuccess() ||
			   machine.getChainerComponent().links().test(player.getOffhandItem()).isSuccess();
	}
	
	private static boolean isHoldingWrench()
	{
		var player = Minecraft.getInstance().player;
		return player.getMainHandItem().is(aztech.modern_industrialization.MITags.WRENCHES) ||
			   player.getOffhandItem().is(aztech.modern_industrialization.MITags.WRENCHES);
	}
}
