from pathlib import Path
import re
root=Path('src/main/java/net/swedz/extended_industrialization')
helper=root/'port/EnergyTransfers.java'
helper.write_text('''package net.swedz.extended_industrialization.port;

import java.util.List;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.swedz.tesseract.neoforge.helper.TransferHelper;

/** Native transactions retain the original equal-share distribution and priority buckets. */
public final class EnergyTransfers {
    @FunctionalInterface public interface Operation<T> { int apply(T target, int amount, TransactionContext transaction); }
    public static <T> int distribute(Operation<T> operation, List<T> targets, int amount, TransactionContext parent) {
        return (int) TransferHelper.distributeLong((target, requested, simulate) -> {
            try (var transaction = Transaction.open(parent)) {
                int transferred = operation.apply(target, (int)requested, transaction);
                if (!simulate) transaction.commit();
                return transferred;
            }
        }, targets, amount, false);
    }
    public static int move(EnergyHandler source, EnergyHandler destination, int maxAmount, TransactionContext parent) {
        int available;
        try (var simulation = Transaction.open(parent)) { available = source.extract(maxAmount, simulation); }
        try (var transaction = Transaction.open(parent)) {
            int inserted = destination.insert(available, transaction);
            int extracted = source.extract(inserted, transaction);
            if (extracted != inserted) return 0;
            transaction.commit();
            return inserted;
        }
    }
}
''')
files=[root/'machines/component/chainer/handler/ChainerEnergyHandler.java', root/'machines/component/chainer/link/linkable/MachineBlockLinkable.java']+list((root/'machines/component/tesla/network').rglob('*.java'))
for p in files:
 s=p.read_text()
 if not any(x in s for x in ['receiveEnergy','extractEnergy','long receive(','long extract(']): continue
 s=s.replace('import dev.technici4n.grandpower.api.EnergyStorageUtil;', 'import net.swedz.extended_industrialization.port.EnergyTransfers;')
 s=s.replace('public long receive(long maxReceive, boolean simulate)', 'public int insert(int maxReceive, TransactionContext transaction)')
 s=s.replace('public long extract(long maxExtract, boolean simulate)', 'public int extract(int maxExtract, TransactionContext transaction)')
 s=s.replace('long receiveEnergy(long maxReceive, boolean simulate)', 'int receiveEnergy(int maxReceive, TransactionContext transaction)')
 s=s.replace('long extractEnergy(long maxExtract, boolean simulate)', 'int extractEnergy(int maxExtract, TransactionContext transaction)')
 s=s.replace('receiveEnergy(maxReceive, simulate)', 'receiveEnergy(maxReceive, transaction)').replace('extractEnergy(maxExtract, simulate)', 'extractEnergy(maxExtract, transaction)')
 s=s.replace('public long getAmount()', 'public long getAmountAsLong()').replace('public long getCapacity()', 'public long getCapacityAsLong()')
 s=s.replace('.getAmount()', '.getAmountAsLong()').replace('.getCapacity()', '.getCapacityAsLong()')
 # EnergyComponent is not an EnergyHandler.
 s=s.replace('EnergyComponent::getCapacityAsLong','EnergyComponent::getCapacity')
 s=s.replace('.canReceive()', '.supportsInsertion()').replace('.canExtract()', '.supportsExtraction()').replace('boolean canReceive()', 'boolean supportsInsertion()').replace('boolean canExtract()', 'boolean supportsExtraction()')
 s=s.replace('input.receive(maxReceive, simulate)', 'input.insert(maxReceive, transaction)').replace('output.extract(maxExtract, simulate)', 'output.extract(maxExtract, transaction)').replace('insertable.receive(maxReceive, simulate)', 'insertable.insert(maxReceive, transaction)').replace('energyStorage.extract(maxExtract, simulate)', 'energyStorage.extract(maxExtract, transaction)')
 s=s.replace('maxReceive = Math.min(maxReceive, maxTransfer.get());', 'maxReceive = (int) Math.min(maxReceive, maxTransfer.get());').replace('maxExtract = Math.min(maxExtract, maxTransfer.get());', 'maxExtract = (int) Math.min(maxExtract, maxTransfer.get());')
 s=s.replace('TransferHelper.distributeLong((wrapper, amount, opSimulate) -> wrapper.handler().receive(amount, opSimulate), wrappers, maxReceive, simulate)', 'EnergyTransfers.distribute((wrapper, amount, tx) -> wrapper.handler().insert(amount, tx), wrappers, maxReceive, transaction)')
 s=s.replace('TransferHelper.distributeLong((wrapper, amount, opSimulate) -> wrapper.handler().extract(amount, opSimulate), wrappers, maxExtract, simulate)', 'EnergyTransfers.distribute((wrapper, amount, tx) -> wrapper.handler().extract(amount, tx), wrappers, maxExtract, transaction)')
 s=s.replace('long amountReceived = 0;', 'int amountReceived = 0;').replace('long remaining = maxReceive;', 'int remaining = maxReceive;')
 s=s.replace('long received = TransferHelper.distributeLong(TeslaReceiver::receiveEnergy, this.cleanBucket(bucket), remaining, simulate);', 'int received = EnergyTransfers.distribute(TeslaReceiver::receiveEnergy, this.cleanBucket(bucket), remaining, transaction);')
 # Transmitter consumes input handlers under the same transaction as receivers.
 if p.name=='TeslaTransmitterComponent.java':
  s=s.replace('long extracted = 0;', 'int extracted = 0;').replace('long remaining = maxExtract - extracted;', 'int remaining = maxExtract - extracted;')
  s=s.replace('energyComponent.consumeEu(remaining, simulate ? Simulation.SIMULATE : Simulation.ACT)', 'energyComponent.buildExtractable(tier -> false).extract(remaining, transaction)')
  s=s.replace('return EnergyStorageUtil.move(energyStorage, network, maxTransmit);','''try (var transaction = net.neoforged.neoforge.transfer.transaction.Transaction.openRoot()) {
                int transferred = EnergyTransfers.move(energyStorage, network, (int)Math.min(Integer.MAX_VALUE, maxTransmit), transaction);
                transaction.commit();
                return transferred;
            }''')
 if p.name=='PlayerTeslaReceiver.java':
  a=s.index('\t\tvar inventory = player.getInventory();');b=s.index('\n\t}',a)
  s=s[:a]+'''        int charged = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize() && charged < maxReceive; slot++) {
            var access = net.neoforged.neoforge.transfer.access.ItemAccess.forPlayerSlot(player, slot);
            var handler = access.getCapability(EnergyApi.ITEM);
            if (handler != null) charged += handler.insert(maxReceive - charged, transaction);
        }
        return charged;'''+s[b:]
 s=s.replace('package '+s.split('package ')[1].split(';')[0]+';', 'package '+s.split('package ')[1].split(';')[0]+';\n\nimport net.neoforged.neoforge.transfer.transaction.TransactionContext;\nimport net.swedz.extended_industrialization.port.EnergyTransfers;')
 # Avoid duplicate imports.
 lines=s.splitlines();seen=set();s='\n'.join(l for l in lines if not (l.startswith('import ') and (l in seen or seen.add(l))))+'\n'
 p.write_text(s)
# Actual item consumption formerly provided by GrandPower; component updates are intentional immediate actions.
p=root/'item/EIEnergyItem.java';p.write_text('''package net.swedz.extended_industrialization.item;
import net.minecraft.world.item.ItemStack;
import net.swedz.tesseract.neoforge.compat.mi.api.SimpleEnergyItem;
public interface EIEnergyItem extends SimpleEnergyItem {
 default boolean tryUseEnergy(ItemStack stack, long amount) {
  long stored = getStoredEnergy(stack);
  if (amount < 0 || stored < amount) return false;
  setStoredEnergy(stack, stored - amount);
  return true;
 }
}
''')
for p in [root/'item/ElectricToolItem.java',root/'item/ElectricArmorItem.java',root/'item/RobotAutoFeederItem.java']:
 s=p.read_text().replace('implements SimpleEnergyItem', 'implements EIEnergyItem').replace(', SimpleEnergyItem,', ', EIEnergyItem,');p.write_text(s)
p=root/'machines/blockentity/UniversalTransformerMachineBlockEntity.java';s=p.read_text().replace('player.getItemInHand(hand).getCapability(EnergyApi.ITEM)', 'net.neoforged.neoforge.transfer.access.ItemAccess.forPlayerInteraction(player, hand).getCapability(EnergyApi.ITEM)').replace('energyItem.receive(energy.getEu() / stackSize, false)', 'energyItem.insert((int)Math.min(Integer.MAX_VALUE, energy.getEu() / stackSize), transaction)');p.write_text(s)
