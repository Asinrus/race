package io.github.asinrus.race.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static io.github.asinrus.race.core.RaceTestSuitRegistry.raceByFutures;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FutureTestSuitRaceTest {

    @Test
    void testSuccessfulCompletion() {
        Future<String> successCompletedFuture = CompletableFuture.completedFuture("Success");

        Map<String, Future<String>> futuresMap = Map.of("op1", successCompletedFuture,
                "op2", successCompletedFuture);

        raceByFutures(futuresMap)
                .withTimeout(Duration.ofSeconds(1))
                .go();
    }

    @Test
    void testTimeoutFailed() throws InterruptedException {
        Future<String> successCompletedFuture = CompletableFuture.completedFuture("Success");

        Map<String, Future<String>> futuresMap = Map.of("op1", successCompletedFuture,
                "op2", CompletableFuture.supplyAsync(() -> {
                    try {
                        sleep(2_000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return "I'm sleep";
                }));

        BoundRaceExecutorImpl.ExecutionException executionException = assertThrows(BoundRaceExecutorImpl.ExecutionException.class,
                () -> raceByFutures(futuresMap).withTimeout(Duration.ofSeconds(1)).go());
        assertEquals("Time out running out, but not all tasks was finished", executionException.getMessage());
    }

    @Test
    void testNoShadowingTheCauseException() {
        class InternalException extends RuntimeException {
            public InternalException(String message) {
                super(message);
            }
        }

        Future<String> failedFuture = CompletableFuture.supplyAsync(
                () -> { throw new InternalException("Internal error"); });

        Map<String, Future<String>> futuresMap = Map.of("op1", failedFuture);

        raceByFutures(futuresMap)
                .withAssertion(res -> {
                    var opResult = res.get("op1");

                    assertTrue(opResult.isHasError());
                    assertInstanceOf(InternalException.class, opResult.error());
                    assertEquals("Internal error", opResult.error().getMessage());
                })
                .go();

    }
}