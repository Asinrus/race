package io.github.asinrus.race.core;

import io.github.asinrus.race.core.domain.result.ComplexExecutionResult;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static io.github.asinrus.race.core.RaceTestSuitRegistry.race;
import static java.util.stream.Collectors.toMap;

public class FutureRaceTestSuit<K, T> implements RaceTestSuit {
    private final Duration timeout;
    private final Consumer<ComplexExecutionResult<K, T>> assertion;
    private final Map<K, Future<T>> completableFutureMap;

    public FutureRaceTestSuit(Duration timeout,
                              Consumer<ComplexExecutionResult<K, T>> assertion,
                              Map<K, Future<T>> completableFutureMap) {
        this.timeout = timeout;
        this.assertion = assertion;
        this.completableFutureMap = completableFutureMap;
    }

    @Override
    public void go() {
        Map<K, Callable<T>> callableMap = completableFutureMap.entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey,
                        e -> {
                            Future<T> comFut = e.getValue();
                            return () -> {
                                try {
                                    return comFut.get();
                                } catch (InterruptedException ex) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeExecutionException(ex);
                                } catch (ExecutionException ex) {
                                    throw new RuntimeExecutionException(ex.getCause());
                                }
                            };
                        }));

        race(callableMap)
                .withTimeout(timeout)
                .withAssertion(assertion)
                .go();
    }

    public static class CompletableFutureTestSuitBuilder<K, T> {

        private Duration timeout = Duration.of(30, ChronoUnit.SECONDS);
        private final Map<K, Future<T>> taskFutures;
        private Consumer<ComplexExecutionResult<K, T>> assertion = (result) -> { };

        public CompletableFutureTestSuitBuilder(Map<K, Future<T>> taskFutures) {
            this.taskFutures = taskFutures;
        }

        public CompletableFutureTestSuitBuilder<K, T> withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public CompletableFutureTestSuitBuilder<K, T> withAssertion(Consumer<ComplexExecutionResult<K, T>> assertion) {
            this.assertion = assertion;
            return this;
        }

        public FutureRaceTestSuit<K, T> go() {
            FutureRaceTestSuit<K, T> testSuit = new FutureRaceTestSuit<>(timeout, assertion, taskFutures);
            testSuit.go();
            return testSuit;
        }
    }

    // TODO: make a new package to store exceptions.
    public static class RuntimeExecutionException extends RuntimeException {

        public RuntimeExecutionException(Throwable cause) {
            super(cause);
        }
    }
}