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

import io.github.asinrus.race.core.domain.result.ComplexExecutionResult;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class represents a test suite for concurrent execution of tasks.
 */
public class BoundRaceTestSuit<K, T> implements RaceTestSuit {

    private final Duration timeout;
    private final Consumer<ComplexExecutionResult<K, T>> assertion;
    private final Map<K, Callable<T>> tasks;

    /**
     * Constructs a new BoundRaceTestSuit with the given duration, assertion, and tasks.
     *
     * @param duration  the maximum time to wait for the tasks to complete
     * @param assertion the assertion to use for validating the results of the tasks
     * @param tasks     the tasks to execute
     */
    public BoundRaceTestSuit(Duration duration,
                             Consumer<ComplexExecutionResult<K, T>> assertion, Map<K, Callable<T>> tasks) {
        this.timeout = duration;
        this.assertion = assertion;
        this.tasks = tasks;
    }

    /**
     * Executes the tasks concurrently and validates the results using the assertion.
     */
    @Override
    public void go() {
        Configuration configuration = Configuration.builder()
                .setTimeout(timeout)
                .build();

        Barrier barrier = new Barrier(configuration);
        var tasksWithBarrier = addBarrier(tasks, barrier);

        BoundRaceExecutorImpl<K, T> executor = new BoundRaceExecutorImpl<>(configuration);
        ComplexExecutionResult<K, T> executionResult = executor.execute(tasksWithBarrier, barrier);

        assertion.accept(executionResult);
    }

    private Callable<T> addBarrier(Callable<T> callable, Barrier barrier) {
        return () -> {
            barrier.latch();
            return callable.call();
        };
    }

    private Map<K, Callable<T>> addBarrier(Map<K, Callable<T>> callableMap, Barrier barrier) {
        return callableMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> addBarrier(e.getValue(), barrier)));
    }

    /**
     * This class represents a builder for creating a BoundRaceTestSuit.
     */
    public static class BoundRaceTestSuitBuilder<K, T> {
        private Duration timeout = Duration.of(30, ChronoUnit.SECONDS);
        private Consumer<ComplexExecutionResult<K, T>> assertion = (t) -> {
        };
        private final Map<K, Callable<T>> task;

        public BoundRaceTestSuitBuilder(Map<K, Callable<T>> task) {
            this.task = task;
        }

        public BoundRaceTestSuitBuilder<K, T> withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public BoundRaceTestSuitBuilder<K, T> withAssertion(Consumer<ComplexExecutionResult<K, T>> assertion) {
            this.assertion = assertion;
            return this;
        }

        public BoundRaceTestSuit<K, T> go() {
            BoundRaceTestSuit<K, T> concurrentExecutionTestSuit =
                    new BoundRaceTestSuit<>(timeout, assertion, task);
            concurrentExecutionTestSuit.go();
            return concurrentExecutionTestSuit;
        }
    }
}