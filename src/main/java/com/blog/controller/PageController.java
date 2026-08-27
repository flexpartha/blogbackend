package com.blog.controller;

import com.blog.entity.Page;
import com.blog.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PageController {

    private final PageRepository pageRepository;

    @GetMapping("/page/{slug}")
    public ResponseEntity<Page> getPageBySlug(@PathVariable String slug) {
        return pageRepository.findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
