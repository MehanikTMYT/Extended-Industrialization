package net.swedz.extended_industrialization.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.item.ElectricToolItem;
import org.apache.commons.lang3.mutable.MutableObject;

@EventBusSubscriber(modid = EI.ID, value = Dist.CLIENT)
public final class ElectricToolHighlight
{
	@SubscribeEvent
	public static void onBlockHighlight(ExtractBlockOutlineRenderStateEvent event)
	{
		var player = Minecraft.getInstance().player;
		if (player == null) return;
		var level = player.level();
		var stack = player.getMainHandItem();
		if(stack.getItem() instanceof ElectricToolItem tool)
		{
			var optionalArea = tool.getArea(level, player, stack, true);
			if(optionalArea.isPresent())
			{
				var area = optionalArea.get();
				var center = area.center();
				
				var fullShape = new MutableObject<>(Shapes.empty());
				
				ElectricToolItem.forEachMineableBlock(level, area, player, (pos, state) ->
				{
					float destroyProgress = state.getDestroyProgress(player, level, pos);
					if(!((double) destroyProgress <= 1.0E-9))
					{
						VoxelShape blockShape = state.getShape(level, pos, CollisionContext.of(player));
						blockShape = blockShape.move(pos.getX() - center.getX(), pos.getY() - center.getY(), pos.getZ() - center.getZ());
						fullShape.setValue(Shapes.joinUnoptimized(fullShape.getValue(), blockShape, BooleanOp.OR));
					}
				});
				
				VoxelShape shape = fullShape.getValue();
				if(shape != Shapes.empty())
				{
					event.addCustomRenderer((outline, buffers, poseStack, translucentPass, levelState) -> {
						var cam = levelState.cameraRenderState.pos;
						net.minecraft.client.renderer.ShapeRenderer.renderShape(
								poseStack,
								buffers.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.lines()),
								shape,
								(double) center.getX() - cam.x,
								(double) center.getY() - cam.y,
								(double) center.getZ() - cam.z,
								0x66000000, 2.0F
						);
						return true;
					});
				}
			}
		}
	}
}
