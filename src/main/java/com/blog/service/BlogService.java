package com.blog.service;

import com.blog.dto.BlogRequest;
import com.blog.dto.BlogResponse;
import com.blog.entity.Blog;
import com.blog.entity.User;
import com.blog.repository.BlogRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    private static final int SHORT_DESC_LENGTH = 150;

    public List<BlogResponse> getAllActiveBlogs() {
        return blogRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Blog getBlogById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
    }

    public List<BlogResponse> getFeaturedBlogs() {
        return blogRepository.findByIsFeaturedTrueAndIsActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BlogResponse> getRecentBlogs() {
        return blogRepository.findTop5ByIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<Blog> getAllBlogsAdmin() {
        return blogRepository.findAll();
    }

    public Blog createBlog(BlogRequest request) {
        Blog blog = Blog.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .image(request.getImage() != null ? java.util.Base64.getDecoder().decode(request.getImage()) : null)
                .isFeatured(request.isFeatured())
                .isActive(request.isActive())
                .userId(request.getUserId())
                .build();
        return blogRepository.save(blog);
    }

    public Blog updateBlog(Long id, BlogRequest request) {
        Blog blog = getBlogById(id);
        blog.setTitle(request.getTitle());
        blog.setDescription(request.getDescription());
        blog.setImage(request.getImage() != null ? Base64.getDecoder().decode(request.getImage()) : null);
        blog.setFeatured(request.isFeatured());
        blog.setActive(request.isActive());
        blog.setUserId(request.getUserId());
        return blogRepository.save(blog);
    }

    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    private BlogResponse toResponse(Blog blog) {
        String desc = blog.getDescription();
        String shortDesc = (desc != null && desc.length() > SHORT_DESC_LENGTH)
                ? desc.substring(0, SHORT_DESC_LENGTH) + "..."
                : desc;

        String author = userRepository.findById(blog.getUserId())
                .map(User::getFirstName)
                .orElse("Unknown");

        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .shortDesc(shortDesc)
                .author(author)
                .image(blog.getImage() != null ? Base64.getEncoder().encodeToString(blog.getImage()) : null)
                .isFeatured(blog.isFeatured())
                .isActive(blog.isActive())
                .createdAt(blog.getCreatedAt())
                .build();
    }
}
