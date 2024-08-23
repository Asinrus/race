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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents the result of a complex execution, which consists of multiple tasks.
 */
public class ComplexExecutionResultImpl<K, T> implements ComplexExecutionResult<K, T> {
    private final Map<K, TaskExecutionResult<T>> answers = new ConcurrentHashMap<>();

    /**
     * Returns the results of the tasks.
     *
     * @return the results of the tasks
     */
    @Override
    public Map<K, TaskExecutionResult<T>> resultMap() {
        return Collections.unmodifiableMap(answers);
    }

    /**
     * Returns the result of the task with the given key.
     *
     * @param key the key of the task
     * @return the result of the task with the given key
     */
    @Override
    public TaskExecutionResult<T> get(K key) {
        return answers.get(key);
    }

    /**
     * Adds the result of a task to this complex execution result.
     *
     * @param key    the key of the task
     * @param result the result of the task
     */
    @Override
    public void addResult(K key, TaskExecutionResult<T> result) {
        answers.put(key, result);
    }
}