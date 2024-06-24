package me.asinrus.race.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static me.asinrus.race.core.RaceTestSuit.race;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BasicRaceTest {
    static private class Holder {
        int i;
    }
    @Test
    void simpleTest() {
        var holder = new Holder();
        race(() ->
                {
                    int i = holder.i++;
                    if (i != 0) {
                        throw new RuntimeException("I'm the second. NOOOOO");
                    }
                    return i;
                })
                .withConfiguration(
                        Configuration.builder()
                                .setNumThreads(2)
                                .setTimeout(Duration.of(4, ChronoUnit.SECONDS))
                                .build()
                )
                .withAssertion(executionResult -> {
                    Collection<Integer> res = executionResult.results();
                    Collection<Throwable> throwable = executionResult.errors();
                    assertFalse(throwable.isEmpty());
                    assertEquals(1, res.size());
                })
                .go();
    }

    @Test
    void simpleBoundedTest() {
        var holder = new Holder();
        Function<Integer, Callable<Integer>> changeFun = (num) -> () -> {
            holder.i = num;
            return holder.i;
        };

        race(Map.of(1, changeFun.apply(1),
                2,changeFun.apply(2)))
                .withAssertion(result ->  {
                        var first = result.get(1);
                        var second = result.get(2);
                        assertFalse(first.isHasError() && second.isHasError());
                })
                .go();
    }
}
