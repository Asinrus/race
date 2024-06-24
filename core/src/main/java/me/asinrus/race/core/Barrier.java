package me.asinrus.race.core;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Barrier {
    private final CountDownLatch countDownLatch;

    public Barrier(Configuration configuration) {
        countDownLatch = new CountDownLatch(configuration.numThreads());
    }

    public void latch() {
        countDownLatch.countDown();
    }

    public boolean awaitAllAchieved(Duration duration) {
        try {
            return countDownLatch.await(duration.toSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new BarrierException(e);
        }

    }

    public static class BarrierException extends RuntimeException {
        public BarrierException(InterruptedException e) {
            super(e);
        }

        public BarrierException(String message) {
            super(message);
        }
    }
}
