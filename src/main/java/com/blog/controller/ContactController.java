package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.entity.Contact;
import com.blog.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/contact/")
    public ResponseEntity<ApiResponse> submitContact(@RequestBody Contact contact) {
        contactService.save(contact);
        return ResponseEntity.ok(ApiResponse.success("Contact saved successfully"));
    }
}
