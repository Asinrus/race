package me.asinrus.race.core;

import me.asinrus.race.core.domain.result.ComplexExecutionResult;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class BoundRaceTestSuit<K, T> implements Race {

    private final Duration timeout;
    private final Consumer<ComplexExecutionResult<K, T>> assertion;
    private final Map<K, Callable<T>> tasks;

    public BoundRaceTestSuit(Duration duration,
                             Consumer<ComplexExecutionResult<K, T>> assertion, Map<K, Callable<T>> tasks) {
        this.timeout = duration;
        this.assertion = assertion;
        this.tasks = tasks;
    }

    @Override
    public void go() {
        Configuration configuration = Configuration.builder()
                .setTimeout(timeout)
                .build();
        Barrier barrier = new Barrier(configuration);
        InitialTaskEnricher taskEnricher = new InitialTaskEnricher();

        Map<K, Callable<T>> taskWithBarrier = taskEnricher.addBarrier(tasks, barrier);

        BoundRaceExecutorImpl<K, T> executor = new BoundRaceExecutorImpl<>(configuration);
        ComplexExecutionResult<K, T> executionResult = executor.execute(taskWithBarrier, barrier);

        assertion.accept(executionResult);
    }

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
