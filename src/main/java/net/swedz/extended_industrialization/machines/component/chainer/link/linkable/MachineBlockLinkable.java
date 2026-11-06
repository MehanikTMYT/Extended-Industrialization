package net.swedz.extended_industrialization.machines.component.chainer.link.linkable;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.extended_industrialization.port.EnergyTransfers;

import aztech.modern_industrialization.api.energy.CableTier;
import aztech.modern_industrialization.api.energy.EnergyApi;
import aztech.modern_industrialization.api.energy.MIEnergyStorage;
import aztech.modern_industrialization.machines.MachineBlock;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.extended_industrialization.machines.blockentity.MachineChainerMachineBlockEntity;
import net.swedz.extended_industrialization.machines.component.chainer.link.ChainerLinkable;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkContext;
import net.swedz.extended_industrialization.machines.component.chainer.link.LinkResult;

public final class MachineBlockLinkable implements ChainerLinkable
{
	@Override
	public boolean matches(LinkContext context)
	{
		if(context.hasItemStack())
		{
			return context.itemStack().getItem() instanceof BlockItem blockItem &&
				   blockItem.getBlock() instanceof MachineBlock;
		}
		else
		{
			return context.hasBlockEntity() &&
				   context.blockEntity() instanceof MachineBlockEntity;
		}
	}
	
	private MIEnergyStorage pickEnergyHandler(boolean input, MIEnergyStorage handlerA, MIEnergyStorage handlerB)
	{
		return handlerA != null && (input ? handlerA.supportsInsertion() : handlerA.supportsExtraction()) ? handlerA :
				handlerB != null && (input ? handlerB.supportsInsertion() : handlerB.supportsExtraction()) ? handlerB : null;
	}
	
	private MIEnergyStorage combine(MIEnergyStorage input, MIEnergyStorage output)
	{
		return input != null || output != null ? new MIEnergyStorage()
		{
			@Override
			public boolean canConnect(CableTier cableTier)
			{
				return input != null ? input.canConnect(cableTier) : output.canConnect(cableTier);
			}
			
			@Override
			public int insert(int maxReceive, TransactionContext transaction)
			{
				return input != null ? input.insert(maxReceive, transaction) : 0;
			}
			
			@Override
			public int extract(int maxExtract, TransactionContext transaction)
			{
				return output != null ? output.extract(maxExtract, transaction) : 0;
			}
			
			@Override
			public long getAmountAsLong()
			{
				return input != null ? input.getAmountAsLong() : 0;
			}
			
			@Override
			public long getCapacityAsLong()
			{
				return input != null ? input.getCapacityAsLong() : 0;
			}
			
			@Override
			public boolean supportsExtraction()
			{
				return output != null;
			}
			
			@Override
			public boolean supportsInsertion()
			{
				return input != null;
			}
		} : null;
	}
	
	@Override
	public LinkResult test(LinkContext context)
	{
		if(context.hasItemStack())
		{
			return LinkResult.success();
		}
		else if(context.hasBlockEntity())
		{
			if(context.blockEntity() instanceof MachineChainerMachineBlockEntity chainerBlockEntity)
			{
				if(chainerBlockEntity.orientation.facingDirection == context.links().direction() ||
				   chainerBlockEntity.orientation.facingDirection.getOpposite() == context.links().direction())
				{
					return LinkResult.fail(true, context.pos());
				}
				if(chainerBlockEntity.getChainerComponent().links().contains(context.links().origin(), true))
				{
					return LinkResult.fail(true, context.pos());
				}
			}
			
			Direction outputDirection = null;
			MachineBlockEntity machineBlockEntity = (MachineBlockEntity) context.blockEntity();
			if(machineBlockEntity.orientation.params.hasOutput)
			{
				outputDirection = machineBlockEntity.orientation.outputDirection;
			}
			
			ResourceHandler<ItemResource> itemHandler = context.level().getCapability(Capabilities.Item.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
			ResourceHandler<FluidResource> fluidHandler = context.level().getCapability(Capabilities.Fluid.BLOCK, context.pos(), context.blockState(), context.blockEntity(), null);
			MIEnergyStorage energyHandlerA = context.level().getCapability(EnergyApi.SIDED, context.pos(), context.blockState(), context.blockEntity(), null);
			MIEnergyStorage energyHandlerB = outputDirection != null ? context.level().getCapability(EnergyApi.SIDED, context.pos(), context.blockState(), context.blockEntity(), outputDirection) : null;
			MIEnergyStorage inputEnergyHandler = this.pickEnergyHandler(true, energyHandlerA, energyHandlerB);
			MIEnergyStorage outputEnergyHandler = this.pickEnergyHandler(false, energyHandlerA, energyHandlerB);
			MIEnergyStorage energyHandler = this.combine(inputEnergyHandler, outputEnergyHandler);
			if(itemHandler != null || fluidHandler != null || energyHandler != null)
			{
				return LinkResult.success(itemHandler, fluidHandler, energyHandler);
			}
		}
		
		return LinkResult.fail(false);
	}
}
