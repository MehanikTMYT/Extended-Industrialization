package net.swedz.extended_industrialization.item.machineconfig;

import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.tesseract.neoforge.tooltip.component.ItemStackTooltipComponent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = EI.ID)
public final class MachineConfigCardItem extends Item
{
	public MachineConfigCardItem(Properties properties)
	{
		super(properties.stacksTo(1));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	private static void onPlaceMachineWithConfig(BlockEvent.EntityPlaceEvent event)
	{
		if(event.getEntity() instanceof Player player)
		{
			var offhand = player.getItemInHand(InteractionHand.OFF_HAND);
			if(offhand.has(EIComponents.MACHINE_CONFIG))
			{
				var config = offhand.get(EIComponents.MACHINE_CONFIG);
				
				var blockEntity = event.getLevel().getBlockEntity(event.getPos());
				if(blockEntity instanceof MachineBlockEntity machine)
				{
					if(config.apply(player, machine, Simulation.SIMULATE))
					{
						config.apply(player, machine, Simulation.ACT);
						player.sendOverlayMessage(EI.text().machineConfigCardApplySuccess());
					}
					else
					{
						player.sendOverlayMessage(EI.text().machineConfigCardApplyFailed());
					}
				}
			}
		}
	}
	
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context)
	{
		var player = context.getPlayer();
		if(player != null)
		{
			var usedHand = context.getHand();
			var itemStack = player.getItemInHand(usedHand);
			var hitBlockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
			if(hitBlockEntity instanceof MachineBlockEntity machine)
			{
				if(!context.getLevel().isClientSide())
				{
					if(player.isShiftKeyDown())
					{
						var config = MachineConfig.from(machine);
						itemStack.set(EIComponents.MACHINE_CONFIG, config);
						player.sendOverlayMessage(EI.text().machineConfigCardSave());
					}
					else
					{
						if(itemStack.has(EIComponents.MACHINE_CONFIG))
						{
							var config = itemStack.get(EIComponents.MACHINE_CONFIG);
							
							if(config.apply(player, machine, Simulation.SIMULATE))
							{
								config.apply(player, machine, Simulation.ACT);
								player.sendOverlayMessage(EI.text().machineConfigCardApplySuccess());
							}
							else
							{
								player.sendOverlayMessage(EI.text().machineConfigCardApplyFailed());
							}
						}
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResult use(Level level, Player player, InteractionHand usedHand)
	{
		if(player.isShiftKeyDown())
		{
			player.getItemInHand(usedHand).remove(EIComponents.MACHINE_CONFIG);
			player.sendOverlayMessage(EI.text().machineConfigCardClear());
			return InteractionResult.SUCCESS;
		}
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.has(EIComponents.MACHINE_CONFIG))
		{
			var machineBlock = stack.get(EIComponents.MACHINE_CONFIG).machineBlock();
			tooltipComponents.accept(EI.text().machineConfigCardConfigured(machineBlock.asItem()));
		}
	}
	
	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		if(stack.has(EIComponents.MACHINE_CONFIG))
		{
			var machineBlock = stack.get(EIComponents.MACHINE_CONFIG).machineBlock();
			var item = machineBlock.asItem();
			return Optional.of(new ItemStackTooltipComponent(item.getDefaultInstance()));
		}
		return Optional.empty();
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(oldStack.getItem());
	}
}
