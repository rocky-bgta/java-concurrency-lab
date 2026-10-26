package dev.rocky.concurrency;

import org.junit.jupiter.api.Test;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class InterruptibleWorkerTest {
    @Test
    void stopsWhenInterruptedWhileWaitingForWork() throws Exception {
        Thread worker = new Thread(new InterruptibleWorker(new LinkedBlockingQueue<>()));
        worker.start();
        try {
            worker.interrupt();
            worker.join(TimeUnit.SECONDS.toMillis(2));
            assertFalse(worker.isAlive(), "Worker must terminate promptly on interruption");
        } finally {
            worker.interrupt();
            worker.join(TimeUnit.SECONDS.toMillis(2));
        }
    }
}
