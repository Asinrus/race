package me.asinrus.race.core;

import me.asinrus.race.core.domain.result.ComplexExecutionResult;
import me.asinrus.race.core.domain.result.ComplexExecutionResultImpl;
import me.asinrus.race.core.domain.result.TaskExecutionResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BoundRaceExecutorImpl<K, T> {
    private final Configuration configuration;

    public BoundRaceExecutorImpl(Configuration configuration) {
        this.configuration = configuration;
    }

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
        if (!barrier.awaitAllAchieved(configuration.timeout())) {
            throw new Barrier.BarrierException("Timeout error");
        }
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

    public static class ExecutionException extends RuntimeException {
        public ExecutionException(InterruptedException exception) {
            super(exception);
        }

        public ExecutionException(String message) {
            super(message);
        }
    }
}
