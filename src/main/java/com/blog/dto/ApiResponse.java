package com.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {
    private String status;
    private String message;

    public static ApiResponse success(String message) {
        return new ApiResponse("success", message);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse("error", message);
    }
}
