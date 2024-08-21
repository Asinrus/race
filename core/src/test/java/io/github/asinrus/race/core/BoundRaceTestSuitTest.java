package io.github.asinrus.race.core;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.Callable;

import static io.github.asinrus.race.core.RaceTestSuitRegistry.race;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundRaceTestSuitTest {

    @Test
    void testSuccessfulExecution() {
        Map<String, Callable<Integer>> tasks = Map.of("task1", () -> 42);

        race(tasks)
                .withAssertion(res -> {
                    var opResult = res.get("task1");
                    assertFalse(opResult.isHasError());
                    assertEquals(42, opResult.result());
                })
                .go();
    }

    @Test
    void testNumberOfThreadsDependsOnMapSize() {
        Map<String, Callable<Integer>> tasks = Map.of("task1", () -> 1,
                "task2", () -> 2);

        race(tasks)
                .withAssertion(res -> {
                    assertEquals(tasks.size(), res.resultMap().size());
                })
                .go();
    }

    @Test
    void testNotShadowingTheException() {
        Map<String, Callable<Integer>> tasksWithException = Map.of(
                "taskWithException", () -> {throw new RuntimeException("Error");},
                "task", () -> 42
        );

        race(tasksWithException)
                .withAssertion(res -> {
                    var taskRes = res.get("task");
                    var taskWithException = res.get("taskWithException");

                    assertFalse(taskRes.isHasError());
                    assertEquals(42, taskRes.result());

                    assertTrue(taskWithException.isHasError());
                    assertEquals("Error", taskWithException.error().getMessage());
                })
                .go();
    }

}