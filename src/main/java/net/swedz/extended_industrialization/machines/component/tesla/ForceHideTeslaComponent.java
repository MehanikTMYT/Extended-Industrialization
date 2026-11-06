package net.swedz.extended_industrialization.machines.component.tesla;

import aztech.modern_industrialization.machines.MachineComponent;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public final class ForceHideTeslaComponent implements MachineComponent
{
	private boolean forceHide;
	
	public boolean isHidden()
	{
		return forceHide;
	}
	
	@Override
	public void writeNbt(net.minecraft.world.level.storage.ValueOutput tag)
	{
		if(forceHide)
		{
			tag.putBoolean("force_hide_tesla", forceHide);
		}
	}
	
	@Override
	public void readNbt(net.minecraft.world.level.storage.ValueInput tag, boolean isUpgradingMachine)
	{
		forceHide = tag.getBooleanOr("force_hide_tesla", false);
	}
}
