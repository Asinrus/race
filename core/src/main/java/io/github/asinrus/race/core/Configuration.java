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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * This class represents a configuration for concurrent execution of tasks.
 */
public class Configuration {

    private final int numThreads;
    private final Duration timeout;

    /**
     * Constructs a new Configuration with the given number of threads and timeout.
     *
     * @param numThreads the number of threads to use for concurrent execution
     * @param timeout the maximum time to wait for the tasks to complete
     */
    private Configuration(int numThreads, Duration timeout) {
        this.numThreads = numThreads;
        this.timeout = timeout;
    }

    /**
     * Returns a new ConfigurationBuilder.
     *
     * @return a new ConfigurationBuilder
     */
    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    /**
     * Returns a default Configuration.
     *
     * @return a default Configuration
     */
    public static Configuration defaultConfiguration() {
        return builder().build();
    }

    /**
     * Returns the number of threads to use for concurrent execution.
     *
     * @return the number of threads to use for concurrent execution
     */
    public int numThreads() {
        return numThreads;
    }

    /**
     * Returns the maximum time to wait for the tasks to complete.
     *
     * @return the maximum time to wait for the tasks to complete
     */
    public Duration timeout() {
        return timeout;
    }

    /**
     * This class represents a builder for creating a Configuration.
     */
    public static class ConfigurationBuilder {
        private Integer numThreads;
        private Duration timeout;

        public ConfigurationBuilder setNumThreads(int numThreads) {
            this.numThreads = numThreads;
            return this;
        }

        public ConfigurationBuilder setTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Returns a new Configuration with the current settings of this builder.
         *
         * @return a new Configuration with the current settings of this builder
         */
        public Configuration build() {
            return new Configuration(
                    Objects.requireNonNullElse(numThreads, 2),
                    Objects.requireNonNullElse(timeout, Duration.of(30, ChronoUnit.SECONDS)));
        }
    }

}