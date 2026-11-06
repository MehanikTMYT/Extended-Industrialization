package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.MIComponents;
import aztech.modern_industrialization.api.energy.CableTier;
import com.google.common.collect.Lists;
import net.swedz.tesseract.neoforge.compat.mi.api.SimpleEnergyItem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIItems;
import net.swedz.extended_industrialization.proxy.modslot.EIModSlot;
import net.swedz.tesseract.api.tuple.Pair;

import java.util.List;

public final class RobotAutoFeederItem extends Item implements EIEnergyItem
{
	private static final long EAT_ENERGY_COST = 256;
	private static final long ENERGY_CAPACITY = CableTier.LV.getMaxTransfer() * EAT_ENERGY_COST;
	
	public RobotAutoFeederItem(Properties properties)
	{
		super(properties
				.stacksTo(1)
				.component(EIComponents.HIDE_BAR, false)
				.component(MIComponents.ENERGY, 0L));
	}
	
	@Override
	public DataComponentType<Long> getEnergyComponent()
	{
		return MIComponents.ENERGY.get();
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	@Override
	public long getEnergyMaxInput(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	@Override
	public long getEnergyMaxOutput(ItemStack stack)
	{
		return ENERGY_CAPACITY;
	}
	
	public boolean hasEnergy(ItemStack stack)
	{
		return this.getStoredEnergy(stack) >= EAT_ENERGY_COST;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !stack.getOrDefault(EIComponents.HIDE_BAR, false);
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.round(this.getStoredEnergy(stack) / (double) ENERGY_CAPACITY * 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		return 0xFF0000;
	}
	
	private Pair<ItemStack, ResourceHandler<ItemResource>> extractCannedFood(Player player)
	{
		List<ItemStack> contents = Lists.newArrayList();
		contents.addAll(player.getInventory().getNonEquipmentItems());
		contents.addAll(EIModSlot.getContents(player, (stack) -> true));
		for(var stack : contents)
		{
			if(stack.is(EIItems.CANNED_FOOD.asItem()))
			{
				var extracted = stack.copyWithCount(1);
				stack.consume(1, player);
				return new Pair<>(extracted, null);
			}
			var capability = ItemAccess.forStack(stack).getCapability(Capabilities.Item.ITEM);
			if(capability != null)
			{
				for(int slot = 0; slot < capability.size(); slot++)
				{
					var resource = capability.getResource(slot);
					if(resource.matches(EIItems.CANNED_FOOD.asItem().getDefaultInstance()))
					{
						try(var transaction = Transaction.openRoot())
						{
							int extractedCount = capability.extract(slot, resource, 1, transaction);
							if(extractedCount > 0)
							{
								transaction.commit();
								return new Pair<>(resource.toStack(extractedCount), capability);
							}
						}
					}
				}
			}
		}
		return new Pair<>(ItemStack.EMPTY, null);
	}
	
	/**
	 * I would just use {@link Player#eat(Level, ItemStack, FoodProperties)} however that conditionally adds to the
	 * inventory on its own and I want to do that myself and there's no good way to prevent double adding.
	 */
	private ItemStack eat(Player player, ItemStack stack, FoodProperties food)
	{
		var level = player.level();
		
		player.getFoodData().eat(food);
		player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
		level.playSound(
				null,
				player.getX(), player.getY(), player.getZ(),
				SoundEvents.PLAYER_BURP,
				SoundSource.PLAYERS,
				0.5f,
				level.getRandom().nextFloat() * 0.1f + 0.9f
		);
		if(player instanceof ServerPlayer serverPlayer)
		{
			CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
		}
		
		var consumable = stack.get(net.minecraft.core.component.DataComponents.CONSUMABLE);
		level.playSound(
				null,
				player.getX(), player.getY(), player.getZ(),
				consumable != null ? consumable.sound().value() : net.minecraft.sounds.SoundEvents.GENERIC_EAT.value(),
				SoundSource.NEUTRAL,
				1,
				1 + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.4f
		);
		if(consumable != null)
		{
			for(var effect : consumable.onConsumeEffects())
			{
				effect.apply(level, stack, player);
			}
		}
		stack.consume(1, player);
		player.gameEvent(GameEvent.EAT);
		
		var remainder = stack.get(net.minecraft.core.component.DataComponents.USE_REMAINDER);
		if(remainder != null && !player.hasInfiniteMaterials())
		{
			return remainder.convertInto().create();
		}
		return ItemStack.EMPTY;
	}
	
	private ItemStack eat(Player player, ItemStack stack)
	{
		var food = stack.get(net.minecraft.core.component.DataComponents.FOOD);
		return food == null ? ItemStack.EMPTY : this.eat(player, stack, food);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel level, Entity entity, net.minecraft.world.entity.EquipmentSlot slot)
	{
		if(level.isClientSide() || level.getGameTime() % 20 != 0)
		{
			return;
		}
		if(entity instanceof Player player &&
		   player.getFoodData().getFoodLevel() <= 18 &&
		   this.hasEnergy(stack))
		{
			var cannedFood = this.extractCannedFood(player);
			
			var itemStack = cannedFood.a();
			var originItemHandler = cannedFood.b();
			
			if(!itemStack.isEmpty())
			{
				var container = this.eat(player, itemStack);
				if(!container.isEmpty())
				{
					if(originItemHandler != null)
					{
						try(var transaction = Transaction.openRoot())
						{
							int inserted = originItemHandler.insert(ItemResource.of(container), container.getCount(), transaction);
							if(inserted > 0)
							{
								transaction.commit();
								container.shrink(inserted);
							}
						}
					}
					
					if(!container.isEmpty() && !player.getInventory().add(container))
					{
						player.drop(container, false);
					}
				}
				this.tryUseEnergy(stack, EAT_ENERGY_COST);
			}
		}
	}
}
