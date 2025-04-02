package com.data.datacache.controller;

import com.data.datacache.model.CacheItem;
import com.data.datacache.service.CacheManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cache Manager", description = "APIs for cache operations")
@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    private final CacheManagerService cacheManagerService;

    @Autowired
    public CacheController(CacheManagerService cacheManagerService) {
        this.cacheManagerService = cacheManagerService;
    }

    @Operation(summary = "Add a CacheItem")
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid CacheItem item) {
        try {
            logger.info("Received add request for: {}", item);
            cacheManagerService.add(item);
            return ResponseEntity.ok("CacheItem added: " + item);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for add: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Server error during add: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Get a CacheItem")
    @PostMapping("/get")
    public ResponseEntity<?> get(@RequestBody CacheItem item) {
        try {
            logger.info("Received get request for: {}", item);
            CacheItem result = cacheManagerService.get(item);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for get: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Server error during get: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Remove a CacheItem")
    @DeleteMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody CacheItem item) {
        try {
            logger.info("Received remove request for: {}", item);
            cacheManagerService.remove(item);
            return ResponseEntity.ok("CacheItem removed: " + item);
        } catch (IllegalArgumentException e) {
            logger.warn("Bad request for remove: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Server error during remove: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Remove all CacheItems")
    @DeleteMapping("/removeAll")
    public ResponseEntity<?> removeAll() {
        try {
            logger.info("Received removeAll request");
            cacheManagerService.removeAll();
            return ResponseEntity.ok("All cache items removed from cache and database");
        } catch (Exception e) {
            logger.error("Server error during removeAll: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @Operation(summary = "Clear cache only")
    @DeleteMapping("/clear")
    public ResponseEntity<?> clear() {
        try {
            logger.info("Received clear request");
            cacheManagerService.clear();
            return ResponseEntity.ok("Cache cleared");
        } catch (Exception e) {
            logger.error("Server error during clear: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
}
