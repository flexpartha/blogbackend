package com.blog.dto;

import lombok.Data;

@Data
public class GoogleCodeRequest {
    private String code;
    private String codeVerifier;
}
