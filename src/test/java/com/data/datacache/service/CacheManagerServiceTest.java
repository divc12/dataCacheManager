package com.data.datacache.service;

import com.data.datacache.model.CacheItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheManagerServiceTest {

    private CacheManagerService cacheManagerService;
    private DatabaseRepository databaseRepository;

    @BeforeEach
    void setUp() {
        databaseRepository = new DatabaseRepository();
        cacheManagerService = new CacheManagerService(databaseRepository);
    }

    @Test
    void testAddAndGet() {
        // Positive test: add item and retrieve it from cache.
        CacheItem item1 = new CacheItem(1, "Content 1");
        cacheManagerService.add(item1);
        CacheItem retrieved = cacheManagerService.get(item1);
        assertEquals(item1, retrieved, "Item should be retrieved from cache");
    }

    @Test
    void testEvictionOnMaxSizeExceeded() {
        // Positive test: test LRU eviction when max size is exceeded.
        CacheItem item1 = new CacheItem(1, "Content 1");
        CacheItem item2 = new CacheItem(2, "Content 2");
        CacheItem item3 = new CacheItem(3, "Content 3");
        cacheManagerService.add(item1);
        cacheManagerService.add(item2);
        cacheManagerService.add(item3);
        cacheManagerService.get(item1); // mark item1 as recently used
        CacheItem item4 = new CacheItem(4, "Content 4");
        cacheManagerService.add(item4);
        // Even if item2 is evicted, get should load it from DB.
        CacheItem retrieved = cacheManagerService.get(item2);
        assertNotNull(retrieved, "Item should be retrievable even after eviction");
    }

    @Test
    void testRemoveItem() {
        // Positive test: remove an item and then ensure it is reloaded from DB.
        CacheItem item1 = new CacheItem(1, "Content 1");
        cacheManagerService.add(item1);
        cacheManagerService.remove(item1);
        CacheItem retrieved = cacheManagerService.get(item1);
        assertEquals(item1, retrieved, "After removal, item should be reloaded from the database");
    }

    @Test
    void testRemoveAll() {
        // Positive test: remove all items and then verify retrieval reloads from DB.
        CacheItem item1 = new CacheItem(1, "Content 1");
        CacheItem item2 = new CacheItem(2, "Content 2");
        cacheManagerService.add(item1);
        cacheManagerService.add(item2);
        cacheManagerService.removeAll();
        CacheItem retrieved = cacheManagerService.get(item1);
        assertEquals(item1, retrieved, "After removeAll, item should be loaded from the database");
    }

    @Test
    void testClearCacheOnly() {
        // Positive test: clear only the cache and then verify get loads from DB.
        CacheItem item1 = new CacheItem(1, "Content 1");
        cacheManagerService.add(item1);
        cacheManagerService.clear();
        CacheItem retrieved = cacheManagerService.get(item1);
        assertEquals(item1, retrieved, "After clearing cache, item should be loaded from the database");
    }

    @Test
    void testAddNullItem() {
        // Negative test: adding a null item should throw an IllegalArgumentException.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cacheManagerService.add(null);
        });
        assertEquals("Cache item cannot be null", exception.getMessage(), "Proper exception message expected");
    }

    @Test
    void testGetNullItem() {
        // Negative test: getting a null item should throw an IllegalArgumentException.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cacheManagerService.get(null);
        });
        assertEquals("Cache item cannot be null", exception.getMessage());
    }

    @Test
    void testRemoveNullItem() {
        // Negative test: removing a null item should throw an IllegalArgumentException.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cacheManagerService.remove(null);
        });
        assertEquals("Cache item cannot be null", exception.getMessage());
    }
}
