package com.speechify.cache;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

/**
 *
 * Use the provided com.speechify.LRUCacheProviderTest in `src/test/java/LruCacheTest.java` to validate your
 * implementation.
 * <p>
 * You may:
 * - Read online API references for Java standard library or JVM collections.
 * You must not:
 * - Read guides about how to code an LRU cache.
 */

public class LRUCacheProvider {

    private LRUCacheProvider() {
        /* This utility class should not be instantiated */
    }

    public static <T> LRUCache<T> createLRUCache(CacheLimits options) {
        final var wrapperHolder = new ConcurrentHashMap<String, OrderWrapper>();
        final var valueHolder = new ConcurrentSkipListMap<OrderWrapper, T>(Comparator.comparing(OrderWrapper::order));
        final var counter = new AtomicLong(Long.MIN_VALUE);
        final var locks = new ConcurrentHashMap<String, Lock>();
        return new OrderWrappedLRUCache<>(options, wrapperHolder, valueHolder, counter, locks);
    }
}
