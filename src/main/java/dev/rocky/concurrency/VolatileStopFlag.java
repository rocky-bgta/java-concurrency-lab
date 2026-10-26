package dev.rocky.concurrency;

/**
 * Demonstrates visibility, not atomicity: volatile publishes a stop request
 * across threads without requiring a synchronized read.
 */
public final class VolatileStopFlag {
    private volatile boolean stopped;

    public void requestStop() {
        stopped = true;
    }

    public boolean isStopped() {
        return stopped;
    }
}
