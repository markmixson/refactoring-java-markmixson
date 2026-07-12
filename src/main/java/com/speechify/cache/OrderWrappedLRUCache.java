package com.speechify.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public record OrderWrappedLRUCache<T>(CacheLimits limits,
                                      ConcurrentHashMap<String, OrderWrapper> wrapperHolder,
                                      ConcurrentSkipListMap<OrderWrapper, T> valueHolder,
                                      AtomicLong counter,
                                      ConcurrentHashMap<String, Lock> locks) implements LRUCache<T> {
    @Override
    public T get(final String key) {
        if (key == null) {
            return null;
        }
        final var lock = locks.computeIfAbsent(key, _ -> new ReentrantLock());
        try {
            lock.lock();
            return doGet(key);
        } finally {
            lock.unlock();
        }
    }

    private T doGet(final String key) {
        final var wrapper = wrapperHolder.remove(key);
        if (wrapper == null) {
            return null;
        }
        final var value = valueHolder.remove(wrapper);
        final var updatedWrapper = new OrderWrapper(key, counter.getAndIncrement());
        wrapperHolder.put(key, updatedWrapper);
        valueHolder.put(updatedWrapper, value);
        return value;
    }

    @Override
    public void set(final String key, final T value) {
        if (key != null) {
            final var lock = locks.computeIfAbsent(key, _ -> new ReentrantLock());
            try {
                lock.lock();
                doSet(key, value);
            } finally {
                lock.unlock();
            }
        }
    }

    private void doSet(final String key, final T value) {
        if (value == null) {
            removeFromCache(key);
        } else {
            if (wrapperHolder.size() == limits.maxItemsCount()) {
                final var entry = valueHolder.pollFirstEntry();
                wrapperHolder.remove(entry.getKey().key());
                locks.remove(entry.getKey().key());
            }
            final var updatedWrapper = new OrderWrapper(key, counter.getAndIncrement());
            valueHolder.put(updatedWrapper, value);
            wrapperHolder.put(key, updatedWrapper);
        }
    }

    private void removeFromCache(final String key) {
        final var wrapper = wrapperHolder.get(key);
        valueHolder.remove(wrapper);
        wrapperHolder.remove(key);
        locks.remove(key);
    }
}
