package dev.rocky.concurrency;

import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentWordCounterTest {
    @Test void concurrentUpdatesAreNotLost() throws Exception {
        ConcurrentWordCounter counter = new ConcurrentWordCounter();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            var tasks = new java.util.ArrayList<Callable<Void>>();
            for (int i = 0; i < 1000; i++) tasks.add(() -> { counter.add("java"); return null; });
            for (Future<Void> f : executor.invokeAll(tasks)) f.get();
            assertEquals(1000, counter.count("java"));
            assertEquals(1000L, counter.snapshot().get("java"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test void rejectsBlankKeys() {
        assertThrows(IllegalArgumentException.class, () -> new ConcurrentWordCounter().add(" "));
    }
}
