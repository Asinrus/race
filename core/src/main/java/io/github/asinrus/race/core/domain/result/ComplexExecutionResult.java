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

import java.util.Map;

/**
 * This interface represents the result of a complex execution, which consists of multiple tasks.
 */
public interface ComplexExecutionResult<K, T> {

    /**
     * Returns the results of the tasks.
     *
     * @return the results of the tasks
     */
    Map<K, TaskExecutionResult<T>> result();

    /**
     * Returns the result of the task with the given key.
     *
     * @param key the key of the task
     * @return the result of the task with the given key
     */
    TaskExecutionResult<T> get(K key);

    /**
     * Adds the result of a task to this complex execution result.
     *
     * @param key the key of the task
     * @param result the result of the task
     */
    void addResult(K key, TaskExecutionResult<T> result);
}