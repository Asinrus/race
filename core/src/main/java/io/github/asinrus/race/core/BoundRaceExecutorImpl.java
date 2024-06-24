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
import io.github.asinrus.race.core.domain.result.ComplexExecutionResultImpl;
import io.github.asinrus.race.core.domain.result.TaskExecutionResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * This class represents an executor that can execute multiple tasks concurrently.
 */
public class BoundRaceExecutorImpl<K, T> {
    private final Configuration configuration;

    /**
     * Constructs a new BoundRaceExecutorImpl with the given configuration.
     *
     * @param configuration the configuration to use for this executor
     */
    public BoundRaceExecutorImpl(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Executes the given tasks concurrently and returns a ComplexExecutionResult that contains the results of the tasks.
     *
     * @param namedTasks the tasks to execute
     * @param barrier the barrier to use for synchronization
     * @return a ComplexExecutionResult that contains the results of the tasks
     */
    public ComplexExecutionResult<K, T> execute(Map<K, Callable<T>> namedTasks, Barrier barrier) {
        ExecutorService executors = Executors.newFixedThreadPool(configuration.numThreads());
        Map<K, Future<T>> futureMap = new HashMap<>();
        namedTasks.forEach((key, call) -> {
            Future<T> future = executors.submit(call);
            futureMap.put(key, future);
        });

        barrierShouldBeAchieved(barrier);
        terminateExecutor(executors);

        return getComplexExecutionResult(futureMap);
    }

    private void barrierShouldBeAchieved(Barrier barrier) {
        barrier.awaitAllAchieved(configuration.timeout());
    }

    private void terminateExecutor(ExecutorService executors) {
        try {
            executors.shutdown();
            boolean isTerminated = executors.awaitTermination(configuration.timeout().toSeconds(), TimeUnit.SECONDS);
            if (!isTerminated) {
                throw new ExecutionException("Time out running out, but not all tasks was finished");
            }
        } catch (InterruptedException e) {
            throw new ExecutionException(e);
        }
    }

    private static <K, T> ComplexExecutionResult<K, T> getComplexExecutionResult(Map<K, Future<T>> futureMap) {
        var complexExecutionResult = new ComplexExecutionResultImpl<K, T>();

        for (var namedFuture : futureMap.entrySet()) {
            var future = namedFuture.getValue();
            var key = namedFuture.getKey();
            complexExecutionResult.addResult(key, TaskExecutionResult.formExecutionResult(future));
        }
        return complexExecutionResult;
    }

    /**
     * This class represents an exception that can be thrown when an execution operation fails.
     */
    public static class ExecutionException extends RuntimeException {
        /**
         * Constructs a new ExecutionException with the specified cause.
         *
         * @param exception the cause
         */
        public ExecutionException(InterruptedException exception) {
            super(exception);
        }

        /**
         * Constructs a new ExecutionException with the specified detail message.
         *
         * @param message the detail message
         */
        public ExecutionException(String message) {
            super(message);
        }
    }
}