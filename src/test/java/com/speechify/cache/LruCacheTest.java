package com.speechify.cache;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.speechify.cache.LRUCacheProvider.createLRUCache;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LruCacheTest {

    @Test
    void getShouldReturnValueForExistingKey() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(10));
        lruCache.set("foo", "bar");
        assertEquals("bar", lruCache.get("foo"));
    }

    @Test
    void getShouldReturnNullForNonExistentKey() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(10));
        lruCache.set("foo", "bar");
        assertNull(lruCache.get("bar"));
        assertNull(lruCache.get(""));
    }

    @Test
    void getShouldReturnValueForManyExistingKeys() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(10));
        lruCache.set("foo", "foo");
        lruCache.set("baz", "baz");
        assertEquals("foo", lruCache.get("foo"));
        assertEquals("baz", lruCache.get("baz"));
    }

    @Test
    void getShouldReturnNullForKeyNotFittingMaxItemsCount() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(1));
        lruCache.set("foo", "bar");
        lruCache.set("baz", "bar");
        assertNull(lruCache.get("foo"));
        assertEquals("bar", lruCache.get("baz"));
    }

    @Test
    void getShouldReturnValueForRecreatedKeyAfterItWasPreviouslyRemoved() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(1));
        lruCache.set("foo", "bar");
        lruCache.set("baz", "bar");
        lruCache.set("foo", "bar");
        assertEquals("bar", lruCache.get("foo"));
        assertNull(lruCache.get("baz"));
    }

    // TODO: getShouldReturnNullForKeyNotFittingMaxItemsCount is the same
    @Test
    void setShouldRemoveOldestKeyOnReachingMaxItemsCountIfNoGetOrHasBeenUsed() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(1));
        lruCache.set("foo", "bar");
        lruCache.set("baz", "bar");
        assertNull(lruCache.get("foo"));
        assertEquals("bar", lruCache.get("baz"));
    }

    @Test
    void setShouldReplaceExistingValueAndValuesForAllKeysAreKeptWhenCacheLimitIsReached() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(3));
        lruCache.set("bax", "par");
        lruCache.set("foo", "bar1");
        lruCache.set("foo", "bar2");
        lruCache.set("foo", "bar3");
        lruCache.set("baz", "bar");

        assertEquals("bar3", lruCache.get("foo"));
        assertEquals("par", lruCache.get("bax"));
        assertEquals("bar", lruCache.get("baz"));
    }

    @Test
    void setShouldRemoveLeastRecentlyUsedKeyOnReachingMaxItemsCount() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(2));
        lruCache.set("foo", "bar");
        lruCache.set("bar", "bar");
        lruCache.get("foo");
        lruCache.set("baz", "bar");

        assertEquals("bar", lruCache.get("foo"));
        assertNull(lruCache.get("bar"));
        assertEquals("bar", lruCache.get("baz"));
    }

    @Test
    void itemIsConsideredAccessedWhenGetIsCalled() {
        LRUCache<String> lruCache = createLRUCache(new CacheLimits(2));
        lruCache.set("1key", "1value");
        lruCache.set("2key", "2value");

        lruCache.get("1key");
        lruCache.set("3key", "3value");

        assertEquals("1value", lruCache.get("1key"));
    }

    @Test
    void removeItem() {
        final var lruCache = createLRUCache(new CacheLimits(2));
        lruCache.set("1key", "1value");
        lruCache.set("2key", "2value");
        lruCache.set("1key", null);
        assertNull(lruCache.get("1key"));
        assertEquals("2value", lruCache.get("2key"));
    }

    @Test
    void nullKeyInsert() {
        final var lruCache = createLRUCache(new CacheLimits(1));
        Assertions.assertDoesNotThrow(() -> lruCache.set(null, null));
    }

    @Test
    void nullKeyGet() {
        final var lruCache = createLRUCache(new CacheLimits(1));
        Assertions.assertNull(lruCache.get(null));
    }

    @Test
    void nullKeySetWithCacheSizeZero() {
        final var lruCache = createLRUCache(new CacheLimits(0));
        Assertions.assertDoesNotThrow(() -> lruCache.set("hi", "blah"));
    }
}
