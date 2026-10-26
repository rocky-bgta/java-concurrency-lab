package dev.rocky.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Fixed-size worker pool with bounded queue and explicit backpressure.
 * Saturated submissions fail fast rather than growing an unbounded backlog.
 */
public final class BoundedTaskExecutor implements AutoCloseable {
    private final ThreadPoolExecutor executor;

    public BoundedTaskExecutor(int workers, int queueCapacity) {
        if (workers < 1 || queueCapacity < 1) {
            throw new IllegalArgumentException("workers and queueCapacity must be positive");
        }
        executor = new ThreadPoolExecutor(workers, workers, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(queueCapacity), new ThreadPoolExecutor.AbortPolicy());
    }

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public int queuedTasks() {
        return executor.getQueue().size();
    }

    public ExecutorService executor() {
        return executor;
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
