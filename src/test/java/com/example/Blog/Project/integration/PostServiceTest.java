package com.example.Blog.Project.integration;


import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.category.repository.CategoryRepository;
import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.repository.PostRepository;
import com.example.Blog.Project.post.service.PostService;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.web.dto.AddPostPayload;
import com.example.Blog.Project.web.dto.AddRolePayload;
import com.example.Blog.Project.web.dto.UpdatePostPayload;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PostServiceTest extends BaseTest{

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @WithMockUser(username = "testuser", authorities = "p.c")
    public void testCreatePost_ShouldCreatePost_WhenValidPayload() {

        Category category = seeder.category("Category");

        AddPostPayload addPostPayload = new AddPostPayload();
        addPostPayload.setTitle("New Post");
        addPostPayload.setContent("Post content");
        addPostPayload.setDescription("Post description");
        addPostPayload.setLogo("Logo");
        addPostPayload.setCategories(Set.of(category));

        Post createdPost = postService.createPost(addPostPayload);

        assertNotNull(createdPost);
        assertEquals("New Post", createdPost.getTitle());
        assertEquals("Post content", createdPost.getContent());
        assertEquals("Post description", createdPost.getDescription());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.c")
    public void testCreatePost_ShouldThrowException_WhenTitleExists() {

        Category category = seeder.category("Category");

        Post existingPost = new Post();
        existingPost.setCategories(Set.of(category));
        existingPost.setAuthor("dummy");
        existingPost.setTitle("Existing Title");
        existingPost.setDescription("Description");
        existingPost.setContent("Content");
        existingPost.setLogo("Logo");
        postRepository.save(existingPost);

        AddPostPayload addPostPayload = new AddPostPayload();
        addPostPayload.setTitle("Existing Title");
        addPostPayload.setContent("New content");
        addPostPayload.setDescription("New description");
        addPostPayload.setLogo("Logo");
        addPostPayload.setCategories(Set.of(category));

        assertThrows(IllegalArgumentException.class, () -> postService.createPost(addPostPayload));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.r")
    public void testGetAllPosts_ShouldReturnAllPosts_WhenCategoryIdIsNull() {

        Category category1 = seeder.category("Tech");
        Category category2 = seeder.category("Health");

        Post post1 = new Post();
        post1.setLogo("Logo1");
        post1.setContent("Content1");
        post1.setTitle("Title1");
        post1.setDescription("Description1");
        post1.setAuthor("dummy");
        post1.setCategories(Set.of(category1));

        Post post2 = new Post();
        post2.setLogo("Logo2");
        post2.setContent("Content2");
        post2.setTitle("Title2");
        post2.setDescription("Description2");
        post2.setAuthor("dummy");
        post2.setCategories(Set.of(category2));

        postRepository.save(post1);
        postRepository.save(post2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postsPage = postService.getAll(pageable, null);

        assertNotNull(postsPage);
        assertEquals(2, postsPage.getTotalElements());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.r")
    public void testGetAllPosts_ShouldReturnPostsForCategory_WhenCategoryIdIsProvided() {

        Category category = seeder.category("Tech");

        Post post1 = new Post();
        post1.setLogo("Logo1");
        post1.setContent("Content1");
        post1.setTitle("Title1");
        post1.setDescription("Description1");
        post1.setAuthor("dummy");
        post1.setCategories(Set.of(category));

        Post post2 = new Post();
        post2.setLogo("Logo2");
        post2.setContent("Content2");
        post2.setTitle("Title2");
        post2.setDescription("Description2");
        post2.setAuthor("dummy");
        post2.setCategories(Set.of(category));

        postRepository.save(post1);
        postRepository.save(post2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postsPage = postService.getAll(pageable, category.getId());

        assertNotNull(postsPage);
        assertEquals(2, postsPage.getTotalElements());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.r")
    public void testFindPost_ShouldReturnPost_WhenPostExists() {

        Category category = seeder.category("Tech");

        Post post = new Post();
        post.setLogo("Logo1");
        post.setContent("Content1");
        post.setTitle("Title1");
        post.setDescription("Description1");
        post.setAuthor("dummy");
        post.setCategories(Set.of(category));

        Post savedPost = postRepository.save(post);

        Post foundPost = postService.findPost(savedPost.getId());

        assertNotNull(foundPost);
        assertEquals(savedPost.getId(), foundPost.getId());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.r")
    public void testFindPost_ShouldThrowException_WhenPostDoesNotExist() {
        long invalidId = 999L;

        assertThrows(EntityNotFoundException.class, () -> postService.findPost(invalidId));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.u")
    public void testUpdatePost_ShouldUpdatePost_WhenValidPayload() {

        Category category = seeder.category("Tech");

        Post post = new Post();
        post.setLogo("Logo1");
        post.setContent("Content1");
        post.setTitle("Title1");
        post.setDescription("Description1");
        post.setAuthor("dummy");
        post.setCategories(Set.of(category));

        Post savedPost = postRepository.save(post);

        UpdatePostPayload updatePayload = new UpdatePostPayload();
        updatePayload.setTitle("Updated Title");
        updatePayload.setContent("Updated Content");
        updatePayload.setDescription("Updated Description");
        updatePayload.setLogo("Updated Logo");
        updatePayload.setCategories(Set.of(category));

        Post updatedPost = postService.updatePost(savedPost.getId(), updatePayload);

        assertNotNull(updatedPost);
        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Content", updatedPost.getContent());
        assertEquals("Updated Description", updatedPost.getDescription());
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.u")
    public void testUpdatePost_ShouldThrowException_WhenPostDoesNotExist() {
        long invalidId = 999L;
        UpdatePostPayload updatePayload = new UpdatePostPayload();
        updatePayload.setTitle("Updated Title");

        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(invalidId, updatePayload));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.d")
    public void testDeletePost_ShouldDeletePost_WhenValidPostId() {

        Category category = seeder.category("Tech");

        Post post = new Post();
        post.setLogo("Logo1");
        post.setContent("Content1");
        post.setTitle("Title1");
        post.setDescription("Description1");
        post.setAuthor("dummy");
        post.setCategories(Set.of(category));
        Post savedPost = postRepository.save(post);

        postService.deletePost(savedPost.getId());

        assertFalse(postRepository.existsById(savedPost.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "p.d")
    public void testDeletePost_ShouldThrowException_WhenPostDoesNotExist() {
        long invalidId = 999L;

        assertThrows(EntityNotFoundException.class, () -> postService.deletePost(invalidId));
    }
}
