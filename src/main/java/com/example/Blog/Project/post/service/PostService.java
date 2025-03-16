package com.example.Blog.Project.post.service;

import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.repository.PostRepository;
import com.example.Blog.Project.security.SecurityService;
import com.example.Blog.Project.web.dto.AddPostPayload;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final SecurityService securityService;

    @Autowired
    public PostService(PostRepository postRepository, SecurityService securityService) {
        this.postRepository = postRepository;
        this.securityService = securityService;
    }

    public Post createPost(AddPostPayload addPostPayload) {
        Optional<Post> byTitle = this.postRepository.findByTitle(addPostPayload.getTitle());

        if (byTitle.isPresent()){
            throw new IllegalArgumentException("Post title already exist!");
        }

        Post newPost = new Post();
        newPost.setAuthor(securityService.getUser().getUsername());
        newPost.setContent(addPostPayload.getContent());
        newPost.setTitle(addPostPayload.getTitle());
        newPost.setDescription(addPostPayload.getDescription());
        newPost.setLogo(addPostPayload.getLogo());
        newPost.setCategories(addPostPayload.getCategories());

        return postRepository.save(newPost);
    }

    public Page<Post> getAll(Pageable pageable, Long categoryId) {
        if (categoryId == null) {
            return postRepository.findAll(pageable);
        }
        return postRepository.findByCategoriesId(categoryId, pageable);
    }

    public Post findPost(long id) {
       return postRepository.findById(id)
               .orElseThrow(() -> new EntityNotFoundException("Post with id [%d] does not exist.".formatted(id)));

    }

    public Post updatePost(long id, Post updatedPost) {
        return postRepository.findById(id).map(post -> {
            post.setTitle(updatedPost.getTitle());
            post.setCategories(updatedPost.getCategories());
            post.setAuthor(securityService.getUser().getUsername());
            post.setContent(updatedPost.getContent());
            post.setCreatedAt(updatedPost.getCreatedAt());
            post.setDescription(updatedPost.getDescription());
            post.setLogo(updatedPost.getLogo());

            return postRepository.save(post);
        }).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public void deletePost(long id) {

        if (!postRepository.existsById(id)){
            throw new IllegalArgumentException("Post not found with id: " + id);
        }

        postRepository.deleteById(id);
    }
}
