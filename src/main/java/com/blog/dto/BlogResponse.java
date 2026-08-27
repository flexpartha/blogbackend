package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BlogResponse {
    private Long id;
    private String title;

    @JsonProperty("short_desc")
    private String shortDesc;

    private String author;
    private String image;

    @JsonProperty("is_featured")
    private boolean isFeatured;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
