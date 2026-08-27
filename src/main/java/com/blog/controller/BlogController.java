package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.BlogRequest;
import com.blog.dto.BlogResponse;
import com.blog.entity.Blog;
import com.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    // ── Public Endpoints ──────────────────────────────────────────────────────

    @GetMapping("/blogs")
    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllActiveBlogs());
    }

    @GetMapping("/blog/{id}")
    public ResponseEntity<Blog> getBlog(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlogById(id));
    }

    @GetMapping("/featured_blogs")
    public ResponseEntity<List<BlogResponse>> getFeaturedBlogs() {
        return ResponseEntity.ok(blogService.getFeaturedBlogs());
    }

    @GetMapping("/recent_blogs")
    public ResponseEntity<List<BlogResponse>> getRecentBlogs() {
        return ResponseEntity.ok(blogService.getRecentBlogs());
    }

    // ── Admin Endpoints (JWT Protected) ───────────────────────────────────────

    @GetMapping("/adminBlogs")
    public ResponseEntity<List<Blog>> getAdminBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogsAdmin());
    }

    @GetMapping("/adminBlog/{id}")
    public ResponseEntity<Blog> getAdminBlog(@PathVariable Long id) {
        return ResponseEntity.ok(blogService.getBlogById(id));
    }

    @PostMapping("/createBlog")
    public ResponseEntity<ApiResponse> createBlog(@Valid @RequestBody BlogRequest request) {
        blogService.createBlog(request);
        return ResponseEntity.ok(ApiResponse.success("Blog created successfully"));
    }

    @PostMapping("/updateBlog/{id}")
    public ResponseEntity<ApiResponse> updateBlog(@PathVariable Long id,
                                                   @Valid @RequestBody BlogRequest request) {
        blogService.updateBlog(id, request);
        return ResponseEntity.ok(ApiResponse.success("Blog updated successfully"));
    }

    @DeleteMapping("/deleteBlog/{id}")
    public ResponseEntity<ApiResponse> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok(ApiResponse.success("Blog deleted successfully"));
    }
}
