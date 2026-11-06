package net.swedz.extended_industrialization.machines.component.chainer.handler;
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
