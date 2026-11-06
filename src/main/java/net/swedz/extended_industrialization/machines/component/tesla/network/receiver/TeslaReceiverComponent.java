package net.swedz.extended_industrialization.machines.component.tesla.network.receiver;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.extended_industrialization.port.EnergyTransfers;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import com.google.common.collect.Lists;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.swedz.extended_industrialization.EI;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaNetwork;
import net.swedz.tesseract.neoforge.api.WorldPos;
import net.swedz.tesseract.neoforge.compat.mi.guicomponent.configurationpanel.ConfigurationPanelBuilder;
import net.swedz.tesseract.neoforge.proxy.Proxies;
import net.swedz.tesseract.neoforge.proxy.builtin.TesseractProxy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TeslaReceiverComponent implements MachineComponent, TeslaReceiver
{
	private static final int PRIORITY_RANGE = 16;
	
	private final MachineBlockEntity machine;
	private final MIEnergyStorage insertable;
	private final Supplier<Boolean> canOperate;
	private final Supplier<CableTier> cableTier;
	
	private Optional<WorldPos> networkKey = Optional.empty();
	
	private int priority = 0;
	
	public TeslaReceiverComponent(MachineBlockEntity machine, MIEnergyStorage energyInsertable,
								  Supplier<Boolean> canOperate, Supplier<CableTier> cableTier)
	{
		this.machine = machine;
		this.insertable = energyInsertable;
		this.canOperate = canOperate;
		this.cableTier = cableTier;
	}
	
	@Override
	public int getPriority()
	{
		return priority;
	}
	
	@Override
	public TeslaReceiverState checkReceiveFrom(TeslaNetwork network)
	{
		if(this.getCableTier() != network.getCableTier())
		{
			return TeslaReceiverState.MISMATCHING_VOLTAGE;
		}
		else if(machine.hasLevel())
		{
			return TeslaReceiver.super.checkReceiveFrom(network);
		}
		else
		{
			return TeslaReceiverState.UNDEFINED;
		}
	}
	
	@Override
	public boolean hasNetwork()
	{
		return networkKey.isPresent();
	}
	
	@Override
	public WorldPos getNetworkKey()
	{
		return networkKey.orElseThrow();
	}
	
	@Override
	public void setNetwork(WorldPos key)
	{
		if(!Proxies.get(TesseractProxy.class).hasServer())
		{
			throw new IllegalStateException("Cannot set network of a receiver from the client");
		}
		
		this.removeFromNetwork();
		
		networkKey = Optional.ofNullable(key);
		
		this.addToNetwork();
	}
	
	@Override
	public WorldPos getPosition()
	{
		return new WorldPos(machine.getLevel(), machine.getBlockPos());
	}
	
	@Override
	public WorldPos getSourcePosition()
	{
		return this.getPosition();
	}
	
	@Override
	public CableTier getCableTier()
	{
		return cableTier.get();
	}
	
	@Override
	public int receiveEnergy(int maxReceive, TransactionContext transaction)
	{
		if(!canOperate.get())
		{
			return 0;
		}
		return insertable.insert(maxReceive, transaction);
	}
	
	@Override
	public long getStoredEnergy()
	{
		return insertable.getAmountAsLong();
	}
	
	@Override
	public long getEnergyCapacity()
	{
		return insertable.getCapacityAsLong();
	}
	
	public void removeFromNetwork()
	{
		if(this.hasNetwork())
		{
			this.getNetwork().remove(this);
		}
	}
	
	public void addToNetwork()
	{
		if(this.hasNetwork())
		{
			this.getNetwork().add(this);
		}
	}
	
	private List<Component> createPriorityTranslations()
	{
		List<Component> lines = Lists.newArrayList();
		for(int priority = -PRIORITY_RANGE; priority <= PRIORITY_RANGE; priority++)
		{
			lines.add(EI.text().priority(priority));
		}
		return lines;
	}
	
	public void appendSelectionPanel(MachineBlockEntity machine, ConfigurationPanelBuilder builder)
	{
		builder.add(
				this.createPriorityTranslations(), false,
				(delta) ->
				{
					int newPriority = priority + delta;
					if(newPriority >= -PRIORITY_RANGE && newPriority <= PRIORITY_RANGE)
					{
						priority = newPriority;
					}
				},
				() -> priority + PRIORITY_RANGE
		);
	}
	
	@Override
	public void writeNbt(net.minecraft.world.level.storage.ValueOutput tag)
	{
		tag.storeNullable("network_key", WorldPos.CODEC, this.hasNetwork() ? this.getNetworkKey() : null);
		tag.putInt("priority", priority);
	}
	
	@Override
	public void readNbt(net.minecraft.world.level.storage.ValueInput tag, boolean isUpgradingMachine)
	{
		this.setNetwork(tag.read("network_key", WorldPos.CODEC).orElse(null));
		priority = Mth.clamp(tag.getIntOr("priority", 0), -PRIORITY_RANGE, PRIORITY_RANGE);
	}
	
	@Override
	public void readClientNbt(net.minecraft.world.level.storage.ValueInput tag)
	{
		networkKey = tag.read("network_key", WorldPos.CODEC);
		priority = Mth.clamp(tag.getIntOr("priority", 0), -PRIORITY_RANGE, PRIORITY_RANGE);
	}
}
