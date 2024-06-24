package me.asinrus.race.core;

import java.util.Map;
import java.util.concurrent.Callable;

public class RaceTestSuit {

    public static <T> RaceExecutionTestSuit.RaceExecutionTestSuitBuilder<T> race(Callable<T> action) {
        return new RaceExecutionTestSuit.RaceExecutionTestSuitBuilder<>(action);
    }

    public static <K, T> BoundRaceTestSuit.BoundRaceTestSuitBuilder<K, T> race(Map<K, Callable<T>> tasks) {
        return new BoundRaceTestSuit.BoundRaceTestSuitBuilder<>(tasks);
    }
}
