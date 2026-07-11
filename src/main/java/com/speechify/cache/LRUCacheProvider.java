package com.speechify.cache;

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
        throw new UnsupportedOperationException("Implement this function");
    }
}
