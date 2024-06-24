package me.asinrus.race.core.domain.result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ComplexExecutionResultImpl<K, T> implements ComplexExecutionResult<K, T> {
    private final Map<K, TaskExecutionResult<T>> answers = new ConcurrentHashMap<>();

    @Override
    public Map<K, TaskExecutionResult<T>> result() {
        return answers;
    }

    @Override
    public TaskExecutionResult<T> get(K key) {
        return answers.get(key);
    }

    @Override
    public void addResult(K key, TaskExecutionResult<T> result) {
        answers.put(key, result);
    }
}
