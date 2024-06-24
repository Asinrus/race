package me.asinrus.race.core.domain.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CommonExecutionResult<T> {
    private final Collection<T> results;
    private final Collection<Throwable> errors;

    public CommonExecutionResult(Collection<T> results, Collection<Throwable> errors) {
        this.results = results;
        this.errors = errors;
    }

    public static <T> CommonExecutionResult<T> parse(ComplexExecutionResult<?, T> executionResult) {
        List<T> result = new ArrayList<>();
        List<Throwable> throwables = new ArrayList<>();
        executionResult.result().forEach((key, val) -> {
            if (val.isHasError()) {
                throwables.add(val.error().getCause());
            } else {
                result.add(val.result());
            }
        });
        return new CommonExecutionResult<>(Collections.unmodifiableList(result),
                Collections.unmodifiableList(throwables));

    }

    public Collection<T> results() {
        return results;
    }

    public Collection<Throwable> errors() {
        return errors;
    }
}
