package com.data.datacache.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "Cache item with an id and content")
public class CacheItem {
	@NotNull(message = "Id is required")
    @Min(value = 1, message = "Id must be greater than 0")
    @Schema(description = "Unique identifier for the cache item", example = "1")
    private Integer id;

    @NotBlank(message = "Content must not be blank")
    @Schema(description = "Content of the cache item", example = "Cached content")
    private String content;

    public CacheItem() {
    }

    public CacheItem(int id, String content) {
        this.id = id;
        this.content = content;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    // equals and hashCode based on id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheItem)) return false;
        CacheItem that = (CacheItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "CacheItem{id=" + id + ", content='" + content + "'}";
    }
}
