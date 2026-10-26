package dev.rocky.concurrency;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/** AtomicLong supports linearizable increments; LongAdder favors high-contention throughput. */
public final class SafeCounter {
    private final AtomicLong exact = new AtomicLong();
    private final LongAdder highThroughput = new LongAdder();

    public void increment() {
        exact.incrementAndGet();
        highThroughput.increment();
    }

    public long exactCount() { return exact.get(); }
    public long throughputCount() { return highThroughput.sum(); }
}
