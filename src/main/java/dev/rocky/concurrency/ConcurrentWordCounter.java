package dev.rocky.concurrency;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/** ConcurrentHashMap.computeIfAbsent safely publishes per-key adders. */
public final class ConcurrentWordCounter {
    private final ConcurrentHashMap<String, LongAdder> counts = new ConcurrentHashMap<>();

    public void add(String word) {
        if (word == null || word.isBlank()) throw new IllegalArgumentException("word is required");
        counts.computeIfAbsent(word, ignored -> new LongAdder()).increment();
    }

    public long count(String word) {
        LongAdder adder = counts.get(word);
        return adder == null ? 0L : adder.sum();
    }

    public Map<String, Long> snapshot() {
        Map<String, Long> result = new java.util.HashMap<>();
        counts.forEach((word, adder) -> result.put(word, adder.sum()));
        return Map.copyOf(result);
    }
}
