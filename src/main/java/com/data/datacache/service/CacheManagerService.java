package com.data.datacache.service;

import com.data.datacache.model.CacheItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class CacheManagerService {

    private static final Logger logger = LoggerFactory.getLogger(CacheManagerService.class);

    private final int maxSize;
    private final DatabaseRepository databaseRepository;
    private final LinkedHashMap<CacheItem, CacheItem> cache;

    @Autowired
    public CacheManagerService(DatabaseRepository databaseRepository) {
        // Configurable maximum size; can be externalized to properties
        this.maxSize = 3;
        this.databaseRepository = databaseRepository;
        // Create a LinkedHashMap in access order for LRU eviction
        this.cache = new LinkedHashMap<CacheItem, CacheItem>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<CacheItem, CacheItem> eldest) {
                if (size() > CacheManagerService.this.maxSize) {
                    try {
                        // Log and attempt to persist the evicted item to the database.
                        logger.info("Evicting item from cache: {}", eldest.getValue());
                        databaseRepository.save(eldest.getValue());
                    } catch (Exception e) {
                        // Log error while saving evicted item.
                        logger.error("Error saving evicted item: {}", e.getMessage());
                    }
                    return true;
                }
                return false;
            }
        };
    }

    // Adds a CacheItem to the cache.
    public void add(CacheItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cache item cannot be null");
        }
        try {
            cache.put(item, item);
            logger.info("Added to cache: {}", item);
        } catch (Exception e) {
            logger.error("Failed to add item to cache: {}", e.getMessage());
            throw new RuntimeException("Failed to add item to cache", e);
        }
    }

    // Retrieves a CacheItem from the cache; if not present, loads it from the database.
    public CacheItem get(CacheItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cache item cannot be null");
        }
        try {
            CacheItem found = cache.get(item);
            if (found != null) {
                logger.info("Found in cache: {}", found);
                return found;
            } else {
                found = databaseRepository.get(item);
                cache.put(item, found);
                logger.info("Loaded into cache from database: {}", found);
                return found;
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve item: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve item", e);
        }
    }

    // Removes a CacheItem from both the cache and database.
    public void remove(CacheItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cache item cannot be null");
        }
        try {
            if (cache.remove(item) != null) {
                logger.info("Removed from cache: {}", item);
            }
            databaseRepository.remove(item);
        } catch (Exception e) {
            logger.error("Failed to remove item: {}", e.getMessage());
            throw new RuntimeException("Failed to remove item", e);
        }
    }

    // Removes all CacheItems from both the cache and database.
    public void removeAll() {
        try {
            cache.clear();
            logger.info("Cleared all entries from cache.");
            databaseRepository.removeAll();
        } catch (Exception e) {
            logger.error("Failed to remove all items: {}", e.getMessage());
            throw new RuntimeException("Failed to remove all items", e);
        }
    }

    // Clears only the internal cache (leaves database intact).
    public void clear() {
        try {
            cache.clear();
            logger.info("Cache cleared, database remains intact.");
        } catch (Exception e) {
            logger.error("Failed to clear cache: {}", e.getMessage());
            throw new RuntimeException("Failed to clear cache", e);
        }
    }
}
