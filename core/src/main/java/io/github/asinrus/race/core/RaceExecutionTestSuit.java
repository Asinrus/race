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

import io.github.asinrus.race.core.domain.result.CommonExecutionResult;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.asinrus.race.core.RaceTestSuitRegistry.race;

/**
 * This class represents a test suite for concurrent execution of a single task.
 */
public class RaceExecutionTestSuit<T> implements RaceTestSuit {
    private final Configuration configuration;
    private final Consumer<CommonExecutionResult<T>> assertion;
    private final Callable<T> task;

    /**
     * Constructs a new RaceExecutionTestSuit with the given configuration, assertion, and task.
     *
     * @param configuration the configuration to use for this test suit
     * @param assertion the assertion to use for validating the result of the task
     * @param task the task to execute
     */
    public RaceExecutionTestSuit(Configuration configuration, Consumer<CommonExecutionResult<T>> assertion, Callable<T> task) {
        this.configuration = configuration;
        this.assertion = assertion;
        this.task = task;
    }

    /**
     * Executes the task concurrently and validates the result using the assertion.
     */
    @Override
    public void go() {
        Map<Integer, Callable<T>> executionTasks = IntStream.range(0, configuration.numThreads())
                .boxed()
                .collect(Collectors
                        .toMap(Function.identity(), i -> task));

        race(executionTasks)
                .withTimeout(configuration.timeout())
                .withAssertion(complexExecutionResult ->
                        {
                            CommonExecutionResult<T> commonExecutionResult = CommonExecutionResult
                                    .parse(complexExecutionResult);
                            assertion.accept(commonExecutionResult);
                        }
                )
                .go();
    }

    /**
     * This class represents a builder for creating a RaceExecutionTestSuit.
     */
    public static class RaceExecutionTestSuitBuilder<T> {
        private Configuration configuration = Configuration.defaultConfiguration();
        private final Callable<T> task;
        private Consumer<CommonExecutionResult<T>> assertion = (t) -> {};

        /**
         */
        public RaceExecutionTestSuitBuilder(Callable<T> task) {
            this.task = task;
        }

        /**
         * @param configuration - configuration of execution - how many threads, timeout
         * @return link to builder
         */
        public RaceExecutionTestSuitBuilder<T> withConfiguration(Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * @return link to builder
         */
        public RaceExecutionTestSuitBuilder<T> withAssertion(Consumer<CommonExecutionResult<T>> assertion) {
            this.assertion = assertion;
            return this;
        }

        /**
         *  Method to run test
         * @return testSuit
         */
        public RaceExecutionTestSuit<T> go() {
            RaceExecutionTestSuit<T> concurrentExecutionTestSuit =
                    new RaceExecutionTestSuit<>(configuration, assertion, task);
            concurrentExecutionTestSuit.go();
            return concurrentExecutionTestSuit;
        }
    }
}