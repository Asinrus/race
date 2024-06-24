package me.asinrus.race.core.domain.result;

import java.util.Map;

public interface ComplexExecutionResult<K, T> {
    Map<K, TaskExecutionResult<T>> result();

    TaskExecutionResult<T> get(K key);

    void addResult(K key, TaskExecutionResult<T> result);
}
