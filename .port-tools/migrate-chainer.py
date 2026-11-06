from pathlib import Path
r=Path('src/main/java/net/swedz/extended_industrialization')
for p in (r/'machines/component/chainer').rglob('*.java'):
 if p.name in ['ChainerItemHandler.java','ChainerFluidHandler.java']:continue
 s=p.read_text().replace('import net.neoforged.neoforge.items.IItemHandler;', 'import net.neoforged.neoforge.transfer.ResourceHandler;\nimport net.neoforged.neoforge.transfer.item.ItemResource;').replace('import net.neoforged.neoforge.fluids.capability.IFluidHandler;', 'import net.neoforged.neoforge.transfer.fluid.FluidResource;')
 s=s.replace('IItemHandler', 'ResourceHandler<ItemResource>').replace('IFluidHandler','ResourceHandler<FluidResource>').replace('Capabilities.ItemHandler.BLOCK','Capabilities.Item.BLOCK').replace('Capabilities.FluidHandler.BLOCK','Capabilities.Fluid.BLOCK')
 p.write_text(s)
base=r/'machines/component/chainer/handler'
(base/'ResourceChainerHandler.java').write_text('''package net.swedz.extended_industrialization.machines.component.chainer.handler;
import java.util.*;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.machines.component.chainer.wrapper.SlotInventoryWrapper;

/** A transaction-preserving concatenated view of linked machine inventories. */
public abstract class ResourceChainerHandler<R extends Resource> extends SlotChainerHandler<ResourceHandler<R>> implements ResourceHandler<R> {
 protected ResourceChainerHandler(ChainerLinks links) { super(links); }
 protected abstract List<ResourceHandler<R>> linkedHandlers();
 protected abstract R emptyResource();
 @Override public void invalidate() {
  var views = new ArrayList<SlotInventoryWrapper<ResourceHandler<R>>>();
  int offset = 0;
  for (var handler : linkedHandlers()) { views.add(new SlotInventoryWrapper<>(handler, offset, handler.size())); offset += handler.size(); }
  wrappers = List.copyOf(views); wrappersSlotMap = new java.util.concurrent.ConcurrentHashMap<>(); slots = offset;
 }
 @Override public int size() { return slots; }
 @Override public R getResource(int index) { var w=getWrapper(index); return w == null ? emptyResource() : w.handler().getResource(w.toLocalSlot(index)); }
 @Override public long getAmountAsLong(int index) { var w=getWrapper(index); return w == null ? 0 : w.handler().getAmountAsLong(w.toLocalSlot(index)); }
 @Override public long getCapacityAsLong(int index, R resource) { var w=getWrapper(index); return w == null ? 0 : w.handler().getCapacityAsLong(w.toLocalSlot(index),resource); }
 @Override public boolean isValid(int index, R resource) { var w=getWrapper(index); return w != null && w.handler().isValid(w.toLocalSlot(index),resource); }
 @Override public int insert(int index, R resource, int amount, TransactionContext tx) {
  if (!chainerLinks.doesAllowOperation()) return 0;
  var w=getWrapper(index); return w == null ? 0 : w.handler().insert(w.toLocalSlot(index),resource,amount,tx);
 }
 @Override public int extract(int index, R resource, int amount, TransactionContext tx) {
  if (!chainerLinks.doesAllowOperation()) return 0;
  var w=getWrapper(index); return w == null ? 0 : w.handler().extract(w.toLocalSlot(index),resource,amount,tx);
 }
}
''')
(base/'ChainerItemHandler.java').write_text('''package net.swedz.extended_industrialization.machines.component.chainer.handler;
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
''')
(base/'ChainerFluidHandler.java').write_text('''package net.swedz.extended_industrialization.machines.component.chainer.handler;
import java.util.*;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.extended_industrialization.machines.component.chainer.ChainerLinks;
import net.swedz.extended_industrialization.port.EnergyTransfers;
public final class ChainerFluidHandler extends ResourceChainerHandler<FluidResource> {
 public ChainerFluidHandler(ChainerLinks links) { super(links); }
 @Override protected List<ResourceHandler<FluidResource>> linkedHandlers() { return getMachineLinks().fluidHandlers(); }
 @Override protected FluidResource emptyResource() { return FluidResource.EMPTY; }
 @Override public int insert(FluidResource resource, int amount, TransactionContext tx) {
  if (!chainerLinks.doesAllowOperation()) return 0;
  return EnergyTransfers.distribute((handler, requested, transaction) -> handler.insert(resource, requested, transaction), linkedHandlers(), amount, tx);
 }
}
''')
p=r/'machines/blockentity/MachineChainerMachineBlockEntity.java';s=p.read_text().replace('Capabilities.ItemHandler.BLOCK','Capabilities.Item.BLOCK').replace('Capabilities.FluidHandler.BLOCK','Capabilities.Fluid.BLOCK');p.write_text(s)
