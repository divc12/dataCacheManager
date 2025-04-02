package com.data.datacache.service;

import com.data.datacache.model.CacheItem;
import org.springframework.stereotype.Service;

@Service
public class DatabaseRepository {
    // In a real implementation, these methods would perform actual database operations.
    public void save(CacheItem item) {
        System.out.println("Saving to database: " + item);
    }

    public CacheItem get(CacheItem item) {
        System.out.println("Retrieving from database: " + item);
        return item;
    }

    public void remove(CacheItem item) {
        System.out.println("Removing from database: " + item);
    }

    public void removeAll() {
        System.out.println("Removing all items from the database");
    }
}

