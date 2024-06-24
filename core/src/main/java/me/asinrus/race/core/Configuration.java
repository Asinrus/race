package me.asinrus.race.core;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Configuration {

    private final int numThreads;
    private final Duration timeout;

    private Configuration(int numThreads, Duration timeout) {
        this.numThreads = numThreads;
        this.timeout = timeout;
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static Configuration defaultConfiguration() {
        return builder().build();
    }

    public int numThreads() {
        return numThreads;
    }

    public Duration timeout() {
        return timeout;
    }

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

        public Configuration build() {
            return new Configuration(
                    Objects.requireNonNullElse(numThreads, 2),
                    Objects.requireNonNullElse(timeout, Duration.of(30, ChronoUnit.SECONDS)));
        }
    }

}
