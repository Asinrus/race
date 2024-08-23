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

package io.github.asinrus.race.core.domain.result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the common result of a complex execution, which consists of multiple tasks.
 */
public class CommonExecutionResult<T> {
    private final Collection<T> results;
    private final Collection<Throwable> errors;

    /**
     * Constructs a new CommonExecutionResult with the given results and errors.
     *
     * @param results the results of the tasks
     * @param errors  the errors that occurred during the tasks execution, if any
     */
    public CommonExecutionResult(Collection<T> results, Collection<Throwable> errors) {
        this.results = results;
        this.errors = errors;
    }

    /**
     * Returns a new CommonExecutionResult based on the given ComplexExecutionResult.
     *
     * @param executionResult the ComplexExecutionResult to base the CommonExecutionResult on
     * @return a new CommonExecutionResult based on the given ComplexExecutionResult
     */
    public static <T> CommonExecutionResult<T> parse(ComplexExecutionResult<?, T> executionResult) {
        List<T> result = new ArrayList<>();
        List<Throwable> throwables = new ArrayList<>();
        executionResult.resultMap().forEach((key, val) -> {
            if (val.isHasError()) {
                throwables.add(val.error().getCause());
            } else {
                result.add(val.result());
            }
        });
        return new CommonExecutionResult<>(Collections.unmodifiableList(result),
                Collections.unmodifiableList(throwables));

    }

    /**
     * Returns the results of the tasks.
     *
     * @return the results of the tasks
     */
    public Collection<T> results() {
        return results;
    }

    /**
     * Returns the errors that occurred during the tasks execution, if any.
     *
     * @return the errors that occurred during the tasks execution, if any
     */
    public Collection<Throwable> errors() {
        return errors;
    }
}