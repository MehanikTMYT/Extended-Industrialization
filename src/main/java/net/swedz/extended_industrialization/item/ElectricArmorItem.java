package net.swedz.extended_industrialization.item;

import aztech.modern_industrialization.MIComponents;
import net.swedz.tesseract.neoforge.compat.mi.api.SimpleEnergyItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.swedz.extended_industrialization.EIComponents;
import net.swedz.tesseract.neoforge.item.ItemHurtHandler;

public class ElectricArmorItem extends net.minecraft.world.item.Item implements EIEnergyItem, ItemHurtHandler
{
	protected final ArmorType type;
	private final ItemAttributeModifiers defaultModifiers;
	public ArmorType getType() { return type; }
	private final long energyCapacity;
	private final long damageCostEnergy;
	
	public ElectricArmorItem(ArmorMaterial material, ArmorType type, Properties properties,
							 long energyCapacity, long damageCostEnergy)
	{
		super(properties.humanoidArmor(material, type)
				.stacksTo(1)
				.component(EIComponents.HIDE_BAR, false)
				.component(MIComponents.ENERGY, 0L));
		this.type = type;
		this.defaultModifiers = material.createAttributes(type);
		this.energyCapacity = energyCapacity;
		this.damageCostEnergy = damageCostEnergy;
	}
	
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack)
	{
		return this.getModifiedDefaultAttributeModifiers(stack, this.hasEnergy(stack) ? defaultModifiers : ItemAttributeModifiers.EMPTY);
	}
	
	public ItemAttributeModifiers getModifiedDefaultAttributeModifiers(ItemStack stack, ItemAttributeModifiers modifiers)
	{
		return modifiers;
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return true;
	}
	
	@Override
	public DataComponentType<Long> getEnergyComponent()
	{
		return MIComponents.ENERGY.get();
	}
	
	@Override
	public long getEnergyCapacity(ItemStack stack)
	{
		return energyCapacity;
	}
	
	@Override
	public long getEnergyMaxInput(ItemStack stack)
	{
		return energyCapacity;
	}
	
	@Override
	public long getEnergyMaxOutput(ItemStack stack)
	{
		return energyCapacity;
	}
	
	public boolean hasEnergy(ItemStack stack)
	{
		return this.getStoredEnergy(stack) >= damageCostEnergy;
	}
	
	@Override
	public boolean isBarVisible(ItemStack stack)
	{
		return !stack.getOrDefault(EIComponents.HIDE_BAR, false);
	}
	
	@Override
	public int getBarWidth(ItemStack stack)
	{
		return (int) Math.round(this.getStoredEnergy(stack) / (double) energyCapacity * 13);
	}
	
	@Override
	public int getBarColor(ItemStack stack)
	{
		return 0xFF0000;
	}
	
	@Override
	public void onHurt(LivingEntity entity, ItemStack stack, int damageAmount)
	{
		if(this.hasEnergy(stack))
		{
			this.tryUseEnergy(stack, damageCostEnergy);
		}
	}
}
