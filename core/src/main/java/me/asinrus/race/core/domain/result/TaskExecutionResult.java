package me.asinrus.race.core.domain.result;

import java.util.concurrent.Future;

public class TaskExecutionResult<T> {
    private final T result;
    private final Throwable error;
    private final boolean isHasError;

    public TaskExecutionResult(T result, Throwable error, boolean isHasError) {
        this.result = result;
        this.error = error;
        this.isHasError = isHasError;
    }

    public static <T> TaskExecutionResult<T> formExecutionResult(Future<T> task) {
        try {
            return new TaskExecutionResult<>(task.get(), null, false);
        } catch (Throwable exception) {
            return new TaskExecutionResult<>(null, exception, true);
        }
    }

    public T result() {
        return result;
    }

    public Throwable error() {
        return error;
    }

    public boolean isHasError() {
        return isHasError;
    }
}