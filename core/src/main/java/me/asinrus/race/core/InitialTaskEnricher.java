package me.asinrus.race.core;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class InitialTaskEnricher {

    public <T> Callable<T> addBarrier(Callable<T> callable, Barrier barrier) {
        return () -> {
            barrier.latch();
            return callable.call();
        };
    }

    public <K, T> Map<K, Callable<T>> addBarrier(Map<K, Callable<T>> callableMap, Barrier barrier) {
        return callableMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> addBarrier(e.getValue(), barrier)));
    }
}
