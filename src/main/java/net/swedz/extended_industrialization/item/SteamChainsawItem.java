package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.blocks.storage.StorageBehaviour;
import aztech.modern_industrialization.items.ContainerItem;
import aztech.modern_industrialization.items.DynamicToolItem;
import aztech.modern_industrialization.items.ItemContainingItemHelper;
import aztech.modern_industrialization.items.SteamDrillFuel;
import net.neoforged.neoforge.transfer.item.ItemResource;
import aztech.modern_industrialization.util.Simulation;
import aztech.modern_industrialization.util.TextHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.swedz.extended_industrialization.EI;
import org.apache.commons.lang3.mutable.Mutable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Most of this code was directly copied from {@link aztech.modern_industrialization.items.SteamDrillItem} but adapted to be an axe-type tool instead and without the 3x3.
 */
@EventBusSubscriber(modid = EI.ID)
public final class SteamChainsawItem extends Item implements DynamicToolItem, ItemContainingItemHelper
{
	public static final StorageBehaviour<ItemResource> BEHAVIOR = new StorageBehaviour<>()
	{
		@Override
		public long getCapacityForResource(ItemResource resource)
		{
			return resource.getMaxStackSize();
		}
		
		public boolean canInsert(ItemResource item)
		{
			return item.toStack().getBurnTime(null, null) > 0;
		}
	};
	
	private static final int FULL_WATER = 18000;
	
	public SteamChainsawItem(Item.Properties settings)
	{
		super(settings
				.stacksTo(1)
				.rarity(Rarity.UNCOMMON)
				.component(MIComponents.SILK_TOUCH, true)
				.component(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY)
				.component(MIComponents.WATER, 0));
	}
	
	private static boolean isNotSilkTouch(net.minecraft.world.item.ItemInstance stack)
	{
		return !stack.getOrDefault(MIComponents.SILK_TOUCH, true);
	}
	
	private static void setSilkTouch(ItemStack stack, boolean silkTouch)
	{
		stack.set(MIComponents.SILK_TOUCH, silkTouch);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
	{
		return !newStack.is(this) || slotChanged;
	}
	
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
	{
		return !newStack.is(this) || !this.canUse(newStack);
	}
	
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state)
	{
		if(this.isSupportedBlock(stack, state) && this.canUse(stack) && !state.is(ToolMaterial.NETHERITE.incorrectBlocksForDrops()))
		{
			return true;
		}
		return super.isCorrectToolForDrops(stack, state);
	}
	
	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state)
	{
		if(this.canUse(stack))
		{
			if(isCorrectToolForDrops(stack, state))
			{
				return ToolMaterial.DIAMOND.speed();
			}
			else
			{
				return 1;
			}
		}
		else
		{
			return 0;
		}
	}
	
	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		if(this.canUse(stack))
		{
			return ItemAttributeModifiers.builder()
					.add(
							Attributes.ATTACK_DAMAGE,
							new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 7, AttributeModifier.Operation.ADD_VALUE),
							EquipmentSlotGroup.MAINHAND
					)
					.build();
		}
		return ItemAttributeModifiers.EMPTY;
	}
	
	@SubscribeEvent
	private static void getAttributeModifiers(ItemAttributeModifierEvent event)
	{
		var stack = event.getItemStack();
		if(stack.getItem() instanceof SteamChainsawItem item &&
		   !item.canUse(stack))
		{
			event.clearModifiers();
		}
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner)
	{
		this.useFuel(stack, miner);
		return true;
	}
	
	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker)
	{
		this.useFuel(stack, attacker);
		return;
	}
	
	public void useFuel(ItemStack stack, LivingEntity entity)
	{
		if(stack.getOrDefault(MIComponents.WATER, 0) > 0 &&
		   stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY).burnTicks() == 0)
		{
			int burnTicks = this.consumeFuel(stack, Simulation.ACT);
			
			stack.set(MIComponents.STEAM_DRILL_FUEL, new SteamDrillFuel(burnTicks, burnTicks));
			
			if(burnTicks > 0 && entity != null)
			{
				entity.level().playSound(
						null,
						entity.getX(), entity.getY(), entity.getZ(),
						SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS,
						1f, 1f
				);
			}
		}
	}
	
	@Override
	public InteractionResult use(Level level, Player user, InteractionHand hand)
	{
		if(hand == InteractionHand.MAIN_HAND && user.isShiftKeyDown())
		{
			var stack = user.getItemInHand(hand);
			setSilkTouch(stack, isNotSilkTouch(stack));
			if(!level.isClientSide())
			{
				user.sendOverlayMessage(
						(isNotSilkTouch(stack) ? MIText.ToolSwitchedNoSilkTouch : MIText.ToolSwitchedSilkTouch).text());
			}
			return InteractionResult.SUCCESS;
		}
		
		var itemStack = user.getItemInHand(hand);
		var hitResult = getPlayerPOVHitResult(level, user, ClipContext.Fluid.ANY);
		if(hitResult.getType() != HitResult.Type.BLOCK)
		{
			return InteractionResult.PASS;
		}
		var fluidState = level.getFluidState(hitResult.getBlockPos());
		if(fluidState.getType() == Fluids.WATER || fluidState.getType() == Fluids.FLOWING_WATER)
		{
			this.fillWater(user, itemStack);
			return InteractionResult.SUCCESS;
		}
		
		return super.use(level, user, hand);
	}
	
	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand)
	{
		var level = interactionTarget.level();
		var blockPos = interactionTarget.blockPosition();
		if(this.canUse(stack) &&
		   stack.is(Tags.Items.TOOLS_SHEAR) && interactionTarget instanceof IShearable shearable)
		{
			if(!level.isClientSide() && shearable.isShearable(player, stack, level, blockPos))
			{
				this.useFuel(stack, player);
				shearable.onSheared(player, stack, level, blockPos)
						.forEach((drop) -> shearable.spawnShearedDrop((net.minecraft.server.level.ServerLevel)level, blockPos, drop));
				interactionTarget.gameEvent(GameEvent.SHEAR, player);
				return InteractionResult.SUCCESS;
			}
			else
			{
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}
	
	private void fillWater(Player player, ItemStack stack)
	{
		if(stack.getOrDefault(MIComponents.WATER, 0) != FULL_WATER)
		{
			stack.set(MIComponents.WATER, FULL_WATER);
			player.playSound(SoundEvents.BUCKET_FILL, 1f, 1f);
		}
	}
	
	@Override
	public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot)
	{
		var fuel = stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY);
		if(fuel.burnTicks() > 0)
		{
			stack.set(MIComponents.STEAM_DRILL_FUEL, new SteamDrillFuel(Math.max(0, fuel.burnTicks() - 5), fuel.maxBurnTicks()));
			stack.update(MIComponents.WATER, 0, (water) -> Math.max(0, water - 5));
		}
		if(fuel.burnTicks() == 0)
		{
			stack.remove(MIComponents.STEAM_DRILL_FUEL);
		}
		if(stack.getOrDefault(MIComponents.WATER, 0) == 0 &&
		   entity instanceof Player player)
		{
			var inv = player.getInventory();
			for(int i = 0; i < inv.getContainerSize(); ++i)
			{
				if(this.tryFillWater(player, stack, inv.getItem(i)))
				{
					break;
				}
			}
		}
	}
	
	public boolean canUse(ItemStack stack)
	{
		if(stack.getOrDefault(MIComponents.WATER, 0) == 0)
		{
			return false;
		}
		return stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY).burnTicks() > 0 ||
			   this.consumeFuel(stack, Simulation.SIMULATE) > 0;
	}
	
	private int consumeFuel(ItemStack stack, Simulation simulation)
	{
		int burnTicks = this.getResource(stack).toStack().getBurnTime(null, null);
		if(burnTicks > 0)
		{
			if(simulation.isActing())
			{
				var burnt = getResource(stack).toStack();
				setAmount(stack, getAmount(stack) - 1);
				
                var remainder = burnt.getCraftingRemainder();
                if (remainder != null && getAmount(stack) == 0) {
                    var remaining = remainder.create();
                    setResource(stack, ItemResource.of(remaining));
                    setAmount(stack, remaining.getCount());
                }
			}
			return burnTicks;
		}
		return 0;
	}
	
	@Override
	public int getEnchantmentLevel(net.minecraft.world.item.ItemInstance stack, Holder<Enchantment> enchantment)
	{
		return this.getAllEnchantmentsFromInstance(stack, enchantment.unwrapLookup()).getLevel(enchantment);
	}
	
	@Override
	public ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup)
	{
        return getAllEnchantmentsFromInstance(stack, lookup);
    }
    private ItemEnchantments getAllEnchantmentsFromInstance(net.minecraft.world.item.ItemInstance stack, HolderLookup.RegistryLookup<Enchantment> lookup) {

		var map = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
		if(!isNotSilkTouch(stack))
		{
			lookup.get(Enchantments.SILK_TOUCH).ifPresent((enchantment) -> map.set(enchantment, 1));
		}
		return map.toImmutable();
	}
	
	@Override
	public boolean isFoil(ItemStack stack)
	{
		return !isNotSilkTouch(stack);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		var stack = context.getItemInHand();
		var level = context.getLevel();
		var pos = context.getClickedPos();
		var state = level.getBlockState(pos);
		var player = context.getPlayer();
		if(this.canUse(stack))
		{
			if(stack.is(ItemTags.AXES))
			{
				var newBlock = StrippingAccess.getStrippedBlocks().get(state.getBlock());
				if(newBlock != null)
				{
					level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1, 1);
					if(!level.isClientSide())
					{
						level.setBlock(pos, newBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)), 11);
						this.useFuel(stack, player);
					}
					return InteractionResult.SUCCESS;
				}
			}
		}
		return super.useOn(context);
	}
	
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack)
	{
		var fuel = stack.getOrDefault(MIComponents.STEAM_DRILL_FUEL, SteamDrillFuel.EMPTY);
		return Optional.of(new SteamChainsawTooltipData(
				stack.getOrDefault(MIComponents.WATER, 0) * 100 / 18000,
				fuel.burnTicks(),
				Math.max(1, fuel.maxBurnTicks()),
				this.getResource(stack),
				this.getAmount(stack)
		));
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack stackBarrel, Slot slot, ClickAction clickType, Player player)
	{
		return handleStackedOnOther(stackBarrel, slot, clickType, player);
	}
	
	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stackBarrel, ItemStack itemStack, Slot slot, ClickAction clickType, Player player,
											SlotAccess cursorStackReference)
	{
		return handleOtherStackedOnMe(stackBarrel, itemStack, slot, clickType, player, cursorStackReference);
	}
	
	@Override
	public boolean handleClick(Player player, ItemStack barrelLike, Mutable<ItemStack> otherStack)
	{
		if(this.tryFillWater(player, barrelLike, otherStack.getValue()))
		{
			return true;
		}
		return ItemContainingItemHelper.super.handleClick(player, barrelLike, otherStack);
	}
	
	private boolean tryFillWater(Player player, ItemStack barrelLike, ItemStack fillSource)
	{
		var nativeStorage = net.neoforged.neoforge.transfer.access.ItemAccess.forStack(fillSource).getCapability(Capabilities.Fluid.ITEM);
		var otherStorage = nativeStorage == null ? null : net.neoforged.neoforge.fluids.capability.IFluidHandler.of(nativeStorage);
		
		if(otherStorage != null)
		{
			long totalWater = 0;
			for(int tank = 0; tank < otherStorage.getTanks(); ++tank)
			{
				if(otherStorage.getFluidInTank(tank).getFluid() == Fluids.WATER)
				{
					totalWater += otherStorage.getFluidInTank(tank).getAmount();
				}
			}
			
			if(totalWater * fillSource.getCount() >= FluidType.BUCKET_VOLUME)
			{
				this.fillWater(player, barrelLike);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> tooltip, TooltipFlag flag)
	{
		var data = (SteamChainsawTooltipData) this.getTooltipImage(stack).orElseThrow();
		
		tooltip.accept(MIText.WaterPercent.text(data.waterLevel).setStyle(TextHelper.WATER_TEXT));
		int barWater = (int) Math.ceil(data.waterLevel / 5d);
		int barVoid = 20 - barWater;
		tooltip.accept(Component.literal("|".repeat(barWater)).setStyle(TextHelper.WATER_TEXT)
				.append(Component.literal("|".repeat(barVoid)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x6b6b6b)))));
		
		if(data.burnTicks > 0)
		{
			tooltip.accept(MIText.SecondsLeft.text(data.burnTicks / 100).setStyle(TextHelper.GRAY_TEXT));
		}
		
		tooltip.accept(MIText.SilkTouchState
				.text(isNotSilkTouch(stack) ? MIText.Deactivated.text().setStyle(TextHelper.RED) : MIText.Activated.text().setStyle(TextHelper.GREEN))
				.setStyle(TextHelper.GRAY_TEXT.withItalic(false)));
	}
	
	@Override
	public StorageBehaviour<ItemResource> getBehaviour()
	{
		return BEHAVIOR;
	}
	
	public record SteamChainsawTooltipData(
			int waterLevel, int burnTicks, int maxBurnTicks, ItemResource variant, long amount
	) implements TooltipComponent
	{
	}
	
	private static class StrippingAccess extends AxeItem
	{
		private StrippingAccess(ToolMaterial material, Properties properties)
		{
			super(material, 0, 0, properties);
		}
		
		public static Map<Block, Block> getStrippedBlocks()
		{
			return AxeItem.STRIPPABLES;
		}
	}
}
