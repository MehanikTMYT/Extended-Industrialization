package net.swedz.extended_industrialization.machines.component.tesla.network.transmitter;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.extended_industrialization.port.EnergyTransfers;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.swedz.extended_industrialization.machines.component.itemslot.TeslaTowerUpgradeComponent;
import net.swedz.extended_industrialization.machines.component.tesla.network.TeslaTransferLimits;
import net.swedz.tesseract.neoforge.api.WorldPos;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class TeslaTransmitterComponent implements MachineComponent.ServerOnly, TeslaTransmitter
{
	private final MachineBlockEntity machine;
	
	private final MIEnergyStorage energyStorage;
	
	private final Supplier<TeslaTransferLimits> limits;
	private final Supplier<WorldPos>            sourcePosition;
	
	private Optional<WorldPos> networkKey = Optional.empty();
	
	public TeslaTransmitterComponent(MachineBlockEntity machine, List<EnergyComponent> energyInputs, Supplier<TeslaTransferLimits> limits, Supplier<WorldPos> sourcePosition)
	{
		this.machine = machine;
		
		// TODO some kind of helper method for this would be nice
		energyStorage = new MIEnergyStorage.NoInsert()
		{
			@Override
			public boolean canConnect(CableTier cableTier)
			{
				return false;
			}
			
			@Override
			public int extract(int maxExtract, TransactionContext transaction)
			{
				int extracted = 0;
				for(var energyComponent : energyInputs)
				{
					int remaining = maxExtract - extracted;
					if(remaining == 0)
					{
						break;
					}
					extracted += energyComponent.buildExtractable(tier -> false).extract(remaining, transaction);
				}
				return extracted;
			}
			
			@Override
			public long getAmountAsLong()
			{
				return energyInputs.stream().mapToLong(EnergyComponent::getEu).sum();
			}
			
			@Override
			public long getCapacityAsLong()
			{
				return energyInputs.stream().mapToLong(EnergyComponent::getCapacity).sum();
			}
			
			@Override
			public boolean supportsExtraction()
			{
				return true;
			}
		};
		
		this.limits = limits;
		this.sourcePosition = sourcePosition;
	}
	
	public TeslaTransmitterComponent(MachineBlockEntity machine, List<EnergyComponent> energyInputs, Supplier<TeslaTransferLimits> limits)
	{
		this(machine, energyInputs, limits, () -> new WorldPos(machine.getLevel(), machine.getBlockPos()));
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
		networkKey = Optional.ofNullable(key);
	}
	
	@Override
	public WorldPos getPosition()
	{
		return new WorldPos(machine.getLevel(), machine.getBlockPos());
	}
	
	@Override
	public WorldPos getSourcePosition()
	{
		return sourcePosition.get();
	}
	
	@Override
	public CableTier getCableTier()
	{
		return limits.get().getCableTier();
	}
	
	@Override
	public long getMaxTransfer()
	{
		return limits.get().getMaxTransfer();
	}
	
	@Override
	public int getMaxDistance()
	{
		return limits.get().getMaxDistance();
	}
	
	@Override
	public long getPassiveDrain()
	{
		return limits.get().getPassiveDrain();
	}
	
	@Override
	public boolean isInterdimensional()
	{
		return machine.components.mapOrDefault(TeslaTowerUpgradeComponent.class, TeslaTowerUpgradeComponent::isInterdimensional, false);
	}
	
	@Override
	public long transmitEnergy(long maxTransmit)
	{
		if(this.hasNetwork())
		{
			var network = this.getNetwork();
			try (var transaction = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
                int transferred = EnergyTransfers.move(energyStorage, network, (int)Math.min(Integer.MAX_VALUE, maxTransmit), transaction);
                transaction.commit();
                return transferred;
            }
		}
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, TransactionContext transaction)
	{
		return energyStorage.extract(maxExtract, transaction);
	}
	
	@Override
	public void writeNbt(net.minecraft.world.level.storage.ValueOutput tag)
	{
	}
	
	@Override
	public void readNbt(net.minecraft.world.level.storage.ValueInput tag, boolean isUpgradingMachine)
	{
	}
}
