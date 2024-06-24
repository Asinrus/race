package me.asinrus.race.core;

import me.asinrus.race.core.domain.result.CommonExecutionResult;
import me.asinrus.race.core.domain.result.ComplexExecutionResult;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RaceExecutionTestSuit<T> implements Race{
    private final Configuration configuration;
    private final Consumer<CommonExecutionResult<T>> assertion;
    private final Callable<T> task;

    public RaceExecutionTestSuit(Configuration configuration, Consumer<CommonExecutionResult<T>> assertion, Callable<T> task) {
        this.configuration = configuration;
        this.assertion = assertion;
        this.task = task;
    }

    @Override
    public void go() {
        Barrier barrier = new Barrier(configuration);
        InitialTaskEnricher taskEnricher = new InitialTaskEnricher();

        Callable<T> taskWithBarrier = taskEnricher.addBarrier(task, barrier);
        CommonExecutionResult<T> executionResult = execute(taskWithBarrier, barrier);
        assertion.accept(executionResult);
    }

    private CommonExecutionResult<T> execute(Callable<T> taskWithBarrier, Barrier barrier) {
        //TODO: simplify?
        Map<Integer, Callable<T>> executionTasks = IntStream.range(0, configuration.numThreads())
                .boxed()
                .collect(Collectors
                        .toMap(Function.identity(), i -> taskWithBarrier));

        BoundRaceExecutorImpl<Integer, T> executor = new BoundRaceExecutorImpl<>(configuration);
        ComplexExecutionResult<Integer, T> complexExecutionResult = executor.execute(executionTasks, barrier);

        return CommonExecutionResult.parse(complexExecutionResult);
    }

    public static class RaceExecutionTestSuitBuilder<T>{
        private Configuration configuration = Configuration.defaultConfiguration();
        private final Callable<T> task;
        private Consumer<CommonExecutionResult<T>> assertion = (t) -> {
        };

        public RaceExecutionTestSuitBuilder(Callable<T> task) {
            this.task = task;
        }

        public RaceExecutionTestSuitBuilder<T> withConfiguration(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        public RaceExecutionTestSuitBuilder<T> withAssertion(Consumer<CommonExecutionResult<T>> assertion) {
            this.assertion = assertion;
            return this;
        }

        public RaceExecutionTestSuit<T> go() {
            RaceExecutionTestSuit<T> concurrentExecutionTestSuit =
                    new RaceExecutionTestSuit<>(configuration, assertion, task);
            concurrentExecutionTestSuit.go();
            return concurrentExecutionTestSuit;
        }
    }
}
