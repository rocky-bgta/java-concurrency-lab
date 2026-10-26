package dev.rocky.concurrency;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class LockProtectedTransferTest {
    @Test
    void oppositeDirectionTransfersPreserveTotalAndAvoidDeadlock() throws Exception {
        var a = new LockProtectedTransfer.Account(1, 1000);
        var b = new LockProtectedTransfer.Account(2, 1000);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<?> left = pool.submit(() -> {
                for (int i = 0; i < 500; i++) LockProtectedTransfer.transfer(a, b, 1);
            });
            Future<?> right = pool.submit(() -> {
                for (int i = 0; i < 500; i++) LockProtectedTransfer.transfer(b, a, 1);
            });
            left.get(5, TimeUnit.SECONDS);
            right.get(5, TimeUnit.SECONDS);
            assertEquals(2000, a.balance() + b.balance());
            assertEquals(1000, a.balance());
            assertEquals(1000, b.balance());
        } finally { pool.shutdownNow(); }
    }

    @Test
    void rejectsInsufficientFunds() {
        var a = new LockProtectedTransfer.Account(1, 2);
        var b = new LockProtectedTransfer.Account(2, 0);
        assertThrows(IllegalArgumentException.class, () -> LockProtectedTransfer.transfer(a, b, 3));
        assertEquals(2, a.balance());
        assertEquals(0, b.balance());
    }
}
