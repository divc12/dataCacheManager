package com.data.datacache.controller;

import com.data.datacache.model.CacheItem;
import com.data.datacache.service.CacheManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CacheController.class)
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CacheManagerService cacheManagerService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddSuccess() throws Exception {
        // Positive test: Successful addition of a CacheItem.
        CacheItem item = new CacheItem(1, "Content 1");
        doNothing().when(cacheManagerService).add(any(CacheItem.class));

        mockMvc.perform(post("/api/cache/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CacheItem added:")));
    }

    @Test
    void testAddInvalidInput() throws Exception {
        // Negative test: Invalid (null) CacheItem causes IllegalArgumentException and returns 400.
        String nullPayload = "{}"; // Missing required properties
        doThrow(new IllegalArgumentException("Cache item cannot be null"))
                .when(cacheManagerService).add(any(CacheItem.class));

        mockMvc.perform(post("/api/cache/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cache item cannot be null")));
    }

    @Test
    void testGetSuccess() throws Exception {
        // Positive test: Successfully retrieving a CacheItem.
        CacheItem item = new CacheItem(1, "Content 1");
        when(cacheManagerService.get(any(CacheItem.class))).thenReturn(item);

        mockMvc.perform(post("/api/cache/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.content").value(item.getContent()));
    }

    @Test
    void testGetInvalidInput() throws Exception {
        // Negative test: Passing invalid input (empty JSON) to get returns 400.
        String nullPayload = "{}";
        doThrow(new IllegalArgumentException("Cache item cannot be null"))
                .when(cacheManagerService).get(any(CacheItem.class));

        mockMvc.perform(post("/api/cache/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cache item cannot be null")));
    }

    @Test
    void testRemoveSuccess() throws Exception {
        // Positive test: Successfully removing a CacheItem.
        CacheItem item = new CacheItem(1, "Content 1");
        doNothing().when(cacheManagerService).remove(any(CacheItem.class));

        mockMvc.perform(delete("/api/cache/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("CacheItem removed:")));
    }

    @Test
    void testRemoveInvalidInput() throws Exception {
        // Negative test: Removing with invalid input returns 400.
        String nullPayload = "{}";
        doThrow(new IllegalArgumentException("Cache item cannot be null"))
                .when(cacheManagerService).remove(any(CacheItem.class));

        mockMvc.perform(delete("/api/cache/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nullPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cache item cannot be null")));
    }

    @Test
    void testRemoveAllSuccess() throws Exception {
        // Positive test: Successfully removing all CacheItems.
        doNothing().when(cacheManagerService).removeAll();

        mockMvc.perform(delete("/api/cache/removeAll"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("All cache items removed")));
    }

    @Test
    void testClearSuccess() throws Exception {
        // Positive test: Successfully clearing the cache.
        doNothing().when(cacheManagerService).clear();

        mockMvc.perform(delete("/api/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cache cleared")));
    }

    @Test
    void testAddServerError5xx() throws Exception {
        // Negative test: Simulate a server error on the add endpoint.
        CacheItem item = new CacheItem(1, "Content 1");
        doThrow(new RuntimeException("Server error during add")).when(cacheManagerService).add(any(CacheItem.class));

        mockMvc.perform(post("/api/cache/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An error occurred: Server error during add")));
    }

    @Test
    void testGetServerError5xx() throws Exception {
        // Negative test: Simulate a server error on the get endpoint.
        CacheItem item = new CacheItem(1, "Content 1");
        doThrow(new RuntimeException("Server error during get")).when(cacheManagerService).get(any(CacheItem.class));

        mockMvc.perform(post("/api/cache/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An error occurred: Server error during get")));
    }

    @Test
    void testRemoveServerError5xx() throws Exception {
        // Negative test: Simulate a server error on the remove endpoint.
        CacheItem item = new CacheItem(1, "Content 1");
        doThrow(new RuntimeException("Server error during remove")).when(cacheManagerService).remove(any(CacheItem.class));

        mockMvc.perform(delete("/api/cache/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An error occurred: Server error during remove")));
    }

    @Test
    void testRemoveAllServerError5xx() throws Exception {
        // Negative test: Simulate a server error on the removeAll endpoint.
        doThrow(new RuntimeException("Server error during removeAll")).when(cacheManagerService).removeAll();

        mockMvc.perform(delete("/api/cache/removeAll"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An error occurred: Server error during removeAll")));
    }

    @Test
    void testClearServerError5xx() throws Exception {
        // Negative test: Simulate a server error on the clear endpoint.
        doThrow(new RuntimeException("Server error during clear")).when(cacheManagerService).clear();

        mockMvc.perform(delete("/api/cache/clear"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An error occurred: Server error during clear")));
    }
    
    @Test
    void testAddInvalidInputReturns400() throws Exception {
        // Negative test: Sending an empty JSON (invalid input) for add should result in a 400 Bad Request.
        String invalidPayload = "{}"; // Missing required fields like id and content
        doThrow(new IllegalArgumentException("Cache item cannot be null"))
                .when(cacheManagerService).add(any(CacheItem.class));

        mockMvc.perform(post("/api/cache/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cache item cannot be null")));
    }

    @Test
    void testGetInvalidInputReturns400() throws Exception {
        // Negative test: Sending an empty JSON for get should also result in a 400 Bad Request.
        String invalidPayload = "{}";
        doThrow(new IllegalArgumentException("Cache item cannot be null"))
                .when(cacheManagerService).get(any(CacheItem.class));

        mockMvc.perform(post("/api/cache/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cache item cannot be null")));
    }

    @Test
    void testRemoveInvalidInputReturns400() throws Exception {
        // Negative test: Attempting to remove with an invalid input should return a 400 Bad Request.
        String invalidPayload = "{}";
        doThrow(new IllegalArgumentException("Cache item cannot be null"))
                .when(cacheManagerService).remove(any(CacheItem.class));

        mockMvc.perform(delete("/api/cache/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cache item cannot be null")));
    }
}
