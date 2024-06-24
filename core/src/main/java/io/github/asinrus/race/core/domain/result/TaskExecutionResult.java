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

package io.github.asinrus.race.core.domain.result;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class represents the result of a task execution.
 */
public class TaskExecutionResult<T> {
    private final T result;
    private final Throwable error;
    private final boolean isHasError;

    /**
     * Constructs a new TaskExecutionResult with the given result, error, and error flag.
     *
     * @param result the result of the task execution
     * @param error the error that occurred during the task execution, if any
     * @param isHasError a flag indicating whether an error occurred during the task execution
     */
    public TaskExecutionResult(T result, Throwable error, boolean isHasError) {
        this.result = result;
        this.error = error;
        this.isHasError = isHasError;
    }

    /**
     * Returns a new TaskExecutionResult based on the given Future.
     *
     * @param task the Future to base the TaskExecutionResult on
     * @param <T> - operation result type
     * @return a new TaskExecutionResult based on the given Future
     */
    public static <T> TaskExecutionResult<T> formExecutionResult(Future<T> task) {
        try {
            return new TaskExecutionResult<>(task.get(), null, false);
        } catch (CancellationException | InterruptedException exception) {
            return new TaskExecutionResult<>(null, exception, true);
        } catch (ExecutionException exception) {
            return new TaskExecutionResult<>(null, exception.getCause(), true);
        }
    }

    /**
     * Returns the result of the task execution.
     *
     * @return the result of the task execution
     */
    public T result() {
        return result;
    }

    /**
     * Returns the error that occurred during the task execution, if any.
     *
     * @return the error that occurred during the task execution, if any
     */
    public Throwable error() {
        return error;
    }

    /**
     * Returns a flag indicating whether an error occurred during the task execution.
     *
     * @return a flag indicating whether an error occurred during the task execution
     */
    public boolean isHasError() {
        return isHasError;
    }
}