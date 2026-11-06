package net.swedz.extended_industrialization.machines.component.chainer.handler;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.extended_industrialization.port.EnergyTransfers;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.InventoryWrapper;
import net.swedz.tesseract.neoforge.helper.TransferHelper;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ChainerEnergyHandler extends ChainerHandler<MIEnergyStorage, InventoryWrapper<MIEnergyStorage>> implements MIEnergyStorage
{
	private final Supplier<CableTier> cableTier;
	private final Supplier<Long>      maxTransfer;
	private final boolean             insertable;
	
	public ChainerEnergyHandler(ChainerLinks chainerLinks, Supplier<CableTier> cableTier, Supplier<Long> maxTransfer, boolean insertable)
	{
		super(chainerLinks);
		this.cableTier = cableTier;
		this.maxTransfer = maxTransfer;
		this.insertable = insertable;
	}
	
	@Override
	public void invalidate()
	{
		List<InventoryWrapper<MIEnergyStorage>> wrappers = Lists.newArrayList();
		
		for(var handler : this.getMachineLinks().energyHandlers())
		{
			if(handler.canConnect(cableTier.get()))
			{
				wrappers.add(new InventoryWrapper<>(handler));
			}
		}
		
		this.wrappers = Collections.unmodifiableList(wrappers);
		this.wrappersSlotMap = Maps.newConcurrentMap();
	}
	
	@Override
	public int insert(int maxReceive, TransactionContext transaction)
	{
		if(!chainerLinks.doesAllowOperation() || !insertable)
		{
			return 0;
		}
		maxReceive = (int) Math.min(maxReceive, maxTransfer.get());
		return EnergyTransfers.distribute((wrapper, amount, tx) -> wrapper.handler().insert(amount, tx), wrappers, maxReceive, transaction);
	}
	
	@Override
	public int extract(int maxExtract, TransactionContext transaction)
	{
		if(!chainerLinks.doesAllowOperation() || insertable)
		{
			return 0;
		}
		maxExtract = (int) Math.min(maxExtract, maxTransfer.get());
		return EnergyTransfers.distribute((wrapper, amount, tx) -> wrapper.handler().extract(amount, tx), wrappers, maxExtract, transaction);
	}
	
	@Override
	public long getAmountAsLong()
	{
		return wrappers.stream().mapToLong((w) -> w.handler().getAmountAsLong()).sum();
	}
	
	@Override
	public long getCapacityAsLong()
	{
		return wrappers.stream().mapToLong((w) -> w.handler().getCapacityAsLong()).sum();
	}
	
	@Override
	public boolean supportsExtraction()
	{
		return !insertable;
	}
	
	@Override
	public boolean supportsInsertion()
	{
		return insertable;
	}
	
	@Override
	public boolean canConnect(CableTier other)
	{
		return cableTier.get() == other;
	}
}
