package net.swedz.extended_industrialization.machines.component.chainer.handler;
import java.util.*;
import aztech.modern_industrialization.inventory.WhitelistedItemStorage;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
public final class ChainerItemHandler extends ResourceChainerHandler<ItemResource> implements WhitelistedItemStorage {
 private boolean whitelisted;
 private Set<Item> whitelist=Set.of();
 public ChainerItemHandler(ChainerLinks links) { super(links); }
 @Override protected List<ResourceHandler<ItemResource>> linkedHandlers() { return getMachineLinks().itemHandlers(); }
 @Override protected ItemResource emptyResource() { return ItemResource.EMPTY; }
 @Override public void invalidate() {
  super.invalidate();
  var items=new HashSet<Item>(); whitelisted=true;
  for (var h:linkedHandlers()) {
   if (h instanceof WhitelistedItemStorage w && w.currentlyWhitelisted()) w.getWhitelistedItems(items);
   else { whitelisted=false; break; }
  }
  whitelist=whitelisted ? Set.copyOf(items) : Set.of();
 }
 @Override public boolean currentlyWhitelisted() { return whitelisted; }
 @Override public void getWhitelistedItems(Set<Item> items) { items.addAll(whitelist); }
}
