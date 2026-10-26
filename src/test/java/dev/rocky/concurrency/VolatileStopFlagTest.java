package dev.rocky.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class VolatileStopFlagTest {
    @Test
    void workerObservesStopRequest() throws Exception {
        VolatileStopFlag flag = new VolatileStopFlag();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch exited = new CountDownLatch(1);
        Thread worker = new Thread(() -> {
            started.countDown();
            while (!flag.isStopped()) {
                Thread.onSpinWait();
            }
            exited.countDown();
        });
        worker.start();
        try {
            assertTrue(started.await(2, TimeUnit.SECONDS));
            flag.requestStop();
            assertTrue(exited.await(2, TimeUnit.SECONDS));
        } finally {
            flag.requestStop();
            worker.join(2000);
        }
        assertFalse(worker.isAlive());
    }
}
