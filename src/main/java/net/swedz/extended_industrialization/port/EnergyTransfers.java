package net.swedz.extended_industrialization.port;

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
