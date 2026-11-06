package net.swedz.extended_industrialization.machines.component.chainer.handler;
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
