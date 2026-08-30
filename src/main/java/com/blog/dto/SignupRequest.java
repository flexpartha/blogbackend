package com.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignupRequest {

    @NotBlank(message = "First name is required")
    @JsonProperty("firstname")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @JsonProperty("lastname")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @JsonProperty("username")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonProperty("password")
    private String password;
}
