package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class BlogRequest {
    @NotBlank
    private String title;

    private String description;

    private String image;

    @JsonProperty("is_featured")
    private boolean isFeatured;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("user_id")
    private Long userId;
}
