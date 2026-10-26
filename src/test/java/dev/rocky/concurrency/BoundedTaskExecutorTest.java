package dev.rocky.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class BoundedTaskExecutorTest {
    @Test
    void rejectsWhenWorkerAndQueueAreBothFull() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        try (BoundedTaskExecutor pool = new BoundedTaskExecutor(1, 1)) {
            pool.submit(() -> {
                started.countDown();
                try { release.await(); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            });
            assertTrue(started.await(2, TimeUnit.SECONDS));
            pool.submit(() -> {});
            assertEquals(1, pool.queuedTasks());
            assertThrows(RejectedExecutionException.class, () -> pool.submit(() -> {}));
        } finally {
            release.countDown();
        }
    }

    @Test
    void rejectsInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new BoundedTaskExecutor(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new BoundedTaskExecutor(1, 0));
    }
}
