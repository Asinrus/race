/*
 * MIT License
 *
 * Copyright (c) 2024 Arkadii Osheev
 *
 * Permission is granted to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of this software, subject to including this copyright notice
 * and permission notice in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 */

package io.github.asinrus.race.core;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * This class provides static methods to create test suits for race conditions.
 */
public class RaceTestSuitRegistry {

    /**
     * Returns a new RaceExecutionTestSuitBuilder for the given action.
     *
     * @param action the action to execute in the test suit
     * @param <T>    operation result type
     * @return a new RaceExecutionTestSuitBuilder for the given action
     */
    public static <T> RaceExecutionTestSuit.RaceExecutionTestSuitBuilder<T> race(Callable<T> action) {
        return new RaceExecutionTestSuit.RaceExecutionTestSuitBuilder<>(action);
    }

    /**
     * Returns a new BoundRaceTestSuitBuilder for the given tasks.
     *
     * @param tasks the tasks to execute in the test suit
     * @param <T>   operation result type
     * @param <K>   key type to specify how to access to the Callable
     * @return a new BoundRaceTestSuitBuilder for the given tasks
     */
    public static <K, T> BoundRaceTestSuit.BoundRaceTestSuitBuilder<K, T> race(Map<K, Callable<T>> tasks) {
        return new BoundRaceTestSuit.BoundRaceTestSuitBuilder<>(tasks);
    }

    public static <K, T> CompletableFutureTestSuit.CompletableFutureTestSuitBuilder<K, T> raceCompletableFutures(Map<K, CompletableFuture<T>> tasks) {
        return new CompletableFutureTestSuit.CompletableFutureTestSuitBuilder<>(tasks);
    }
}