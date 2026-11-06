package net.swedz.extended_industrialization.machines.component.solar.electric;

import aztech.modern_industrialization.inventory.ConfigurableFluidStack;
import aztech.modern_industrialization.inventory.ConfigurableItemStack;
import aztech.modern_industrialization.inventory.MIInventory;
import aztech.modern_industrialization.machines.MachineComponent;
import aztech.modern_industrialization.machines.components.EnergyComponent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import aztech.modern_industrialization.util.Simulation;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.extended_industrialization.EIFluids;
import net.swedz.extended_industrialization.component.PhotovoltaicCell;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class SolarGeneratorComponent implements MachineComponent.ServerOnly
{
	private final MIInventory     inventory;
	private final EnergyComponent energy;
	
	private final Supplier<Float>             energyEfficiency;
	private final Predicate<PhotovoltaicCell> photovoltaicCellTest;
	
	private int tick;
	
	private PhotovoltaicCell photovoltaicCell;
	
	private boolean usedDistilledWater;
	
	public SolarGeneratorComponent(MIInventory inventory, EnergyComponent energy, Supplier<Float> energyEfficiency, Predicate<PhotovoltaicCell> photovoltaicCellTest)
	{
		this.inventory = inventory;
		this.energy = energy;
		this.energyEfficiency = energyEfficiency;
		this.photovoltaicCellTest = photovoltaicCellTest;
	}
	
	private ConfigurableFluidStack getSlotWater()
	{
		return inventory.getFluidStacks().getFirst();
	}
	
	private ConfigurableItemStack getSlotPhotovoltaicCell()
	{
		return inventory.getItemStacks().getFirst();
	}
	
	private PhotovoltaicCell getPhotovoltaicCellData()
	{
		var resource = this.getSlotPhotovoltaicCell().getResource();
		var components = PatchedDataComponentMap.fromPatch(resource.getItem().components(), resource.getComponentsPatch());
		return components.get(EIComponents.PHOTOVOLTAIC_CELL.get());
	}
	
	public float getEnergyEfficiency()
	{
		float efficiency = energyEfficiency.get();
		return photovoltaicCell != null ?
				Math.max(efficiency, photovoltaicCell.minimumEfficiency()) :
				efficiency;
	}
	
	public long getEnergyPerTick()
	{
		return photovoltaicCell != null ? (long) (photovoltaicCell.euPerTick() * this.getEnergyEfficiency() * (usedDistilledWater ? 1.5 : 1)) : 0;
	}
	
	private boolean tryUseDistilledWater()
	{
		var water = this.getSlotWater();
		if(water.getAmount() > 0 && water.getResource().is(EIFluids.DISTILLED_WATER.asFluid()))
		{
			water.decrement(1);
			return true;
		}
		return false;
	}
	
	private void incrementSolarTicks(ItemStack stack)
	{
		int solarTicks = stack.getOrDefault(EIComponents.SOLAR_TICKS, 0) + 1;
		if(solarTicks > photovoltaicCell.lifetimeTicks())
		{
			return;
		}
		stack.set(EIComponents.SOLAR_TICKS, solarTicks);
	}
	
	private int getSolarTicksRemaining(ItemStack stack)
	{
		int solarTicks = stack.getOrDefault(EIComponents.SOLAR_TICKS, 0);
		return photovoltaicCell.lifetimeTicks() - solarTicks;
	}
	
	private void deterioratePhotovoltaicCell()
	{
		var slotCell = this.getSlotPhotovoltaicCell();
		var cellStack = slotCell.toStack();
		this.incrementSolarTicks(cellStack);
		if(this.getSolarTicksRemaining(cellStack) > 0)
		{
			slotCell.setKey(ItemResource.of(cellStack));
		}
		else
		{
			photovoltaicCell = null;
			usedDistilledWater = false;
			slotCell.setAmount(0);
			slotCell.setKey(ItemResource.EMPTY);
		}
	}
	
	public void tick()
	{
		tick++;
		if(tick > 20)
		{
			tick = 1;
		}
		
		var photovoltaicCellComponent = this.getPhotovoltaicCellData();
		if(photovoltaicCellComponent != null && photovoltaicCellTest.test(photovoltaicCellComponent))
		{
			photovoltaicCell = photovoltaicCellComponent;
			
			usedDistilledWater = this.tryUseDistilledWater();
			boolean deterioratePhotovoltaicCell = (!usedDistilledWater || tick % 2 == 0) && !photovoltaicCell.lastsForever();
			
			if(deterioratePhotovoltaicCell)
			{
				this.deterioratePhotovoltaicCell();
			}
			
			energy.insertEu(this.getEnergyPerTick(), Simulation.ACT);
		}
		else
		{
			photovoltaicCell = null;
			
			usedDistilledWater = false;
		}
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
