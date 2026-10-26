package dev.rocky.concurrency;

import java.util.concurrent.BlockingQueue;

/** Cooperatively terminates on interruption without swallowing the signal. */
public final class InterruptibleWorker implements Runnable {
    private final BlockingQueue<String> queue;

    public InterruptibleWorker(BlockingQueue<String> queue) {
        this.queue = java.util.Objects.requireNonNull(queue);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                process(queue.take());
            }
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private void process(String item) {
        // Replace with application-specific work; take() demonstrates interruptible waiting.
        java.util.Objects.requireNonNull(item);
    }
}
