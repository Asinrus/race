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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads completes.
 */
public class Barrier {
    private final CountDownLatch countDownLatch;

    /**
     * Constructs a new Barrier with the given configuration.
     *
     * @param configuration the configuration to use for this barrier
     */
    public Barrier(Configuration configuration) {
        countDownLatch = new CountDownLatch(configuration.numThreads());
    }

    /**
     * Decrements the count of the latch, signaling that one more thread has completed its task.
     */
    public void latch() {
        countDownLatch.countDown();
    }

    /**
     * Causes the current thread to wait until the latch has counted down to zero, unless the thread is interrupted, or the specified waiting time elapses.
     *
     * @param duration the maximum time to wait
     * @throws BarrierException if the current thread is interrupted while waiting
     */
    public void awaitAllAchieved(Duration duration) {
        try {
            boolean isAllAchieved = countDownLatch.await(duration.toSeconds(), TimeUnit.SECONDS);
            if (!isAllAchieved) {
                throw new BarrierException("Timeout error");
            }
        } catch (InterruptedException e) {
            throw new BarrierException(e);
        }
    }

    /**
     * This class represents an exception that can be thrown when a barrier operation fails.
     */
    public static class BarrierException extends RuntimeException {
        /**
         * Constructs a new BarrierException with the specified cause.
         *
         * @param e the cause
         */
        public BarrierException(InterruptedException e) {
            super(e);
        }

        /**
         * Constructs a new BarrierException with the specified detail message.
         *
         * @param message the detail message
         */
        public BarrierException(String message) {
            super(message);
        }
    }
}