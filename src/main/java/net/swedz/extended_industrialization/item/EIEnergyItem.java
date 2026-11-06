package net.swedz.extended_industrialization.item;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.compat.mi.api.SimpleEnergyItem;
public interface EIEnergyItem extends SimpleEnergyItem {
 default long getStoredEnergy(net.minecraft.world.item.ItemInstance stack) { return stack.getOrDefault(getEnergyComponent(), 0L); }
 default boolean tryUseEnergy(ItemStack stack, long amount) {
  long stored = getStoredEnergy(stack);
  if (amount < 0 || stored < amount) return false;
  setStoredEnergy(stack, stored - amount);
  return true;
 }
}
