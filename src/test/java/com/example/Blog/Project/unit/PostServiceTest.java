package com.example.Blog.Project.unit;

import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.repository.PostRepository;
import com.example.Blog.Project.post.service.PostService;
import com.example.Blog.Project.security.SecurityService;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.web.dto.AddPostPayload;
import com.example.Blog.Project.web.dto.UpdatePostPayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SecurityService securityService;

    @Test
    public void testFindPostWhenIdDoesNotExist() {
        when(postRepository.findById(1L)).thenThrow(new EntityNotFoundException("Post with id [%d] does not exist.".formatted(1L)));

        Throwable result = Assertions.assertThrows(EntityNotFoundException.class, () -> postService.findPost(1L));
        Assertions.assertEquals("Post with id [1] does not exist.", result.getMessage());
    }

    @Test
    public void testFindPostWhenIdExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));

        Optional<Post> postOpt = postRepository.findById(1L);
        Assertions.assertTrue(postOpt.isPresent());
    }

    @Test
    public void testGetAllPostsWhenCategoryIdIsNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Post post = new Post();
        post.setTitle("Test Post");

        Page<Post> mockPage = new PageImpl<>(List.of(post));
        when(postRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Post> result = postService.getAll(pageable, null);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Test Post", result.getContent().get(0).getTitle());
    }

    @Test
    public void testGetAllPostsByCategoryId() {
        Pageable pageable = PageRequest.of(0, 10);
        Post post = new Post();
        post.setTitle("Category Post");

        Page<Post> mockPage = new PageImpl<>(List.of(post));
        when(postRepository.findByCategoriesId(1L, pageable)).thenReturn(mockPage);

        Page<Post> result = postService.getAll(pageable, 1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Category Post", result.getContent().get(0).getTitle());

    }

    @Test
    public void testCreatePostWhenTitleIsAlreadyPresent() {
        String title = "dummy";

        when(postRepository.findByTitle(title)).thenReturn(Optional.of(new Post()));

        AddPostPayload postPayload = new AddPostPayload();
        postPayload.setTitle(title);
        Throwable result = Assertions.assertThrows(IllegalArgumentException.class, () -> postService.createPost(postPayload));
        Assertions.assertEquals("Post title already exist!", result.getMessage());
    }

    @Test
    public void testCreatePost() {
        String title = "dummy";
        AddPostPayload postPayload = new AddPostPayload();
        postPayload.setTitle(title);
        postPayload.setLogo("https://dummy.logo.com");
        postPayload.setContent("dummy");
        postPayload.setDescription("dummy description");

        User user = new User();
        user.setUsername("Dummy");

        when(postRepository.save(any())).then(v -> v.getArguments()[0]);
        when(postRepository.findByTitle(title)).thenReturn(Optional.empty());
        when(securityService.getUser()).thenReturn(user);

        Post createdPost = postService.createPost(postPayload);

        Assertions.assertNotNull(createdPost);
        Assertions.assertEquals(user.getUsername(), createdPost.getAuthor());
    }

    @Test
    public void testUpdatePostWhenPostIdNotExist() {
        long postId = 1L;
        UpdatePostPayload updatePayload = new UpdatePostPayload();
        updatePayload.setTitle("New Title");

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Throwable result = Assertions.assertThrows(EntityNotFoundException.class,
                () -> postService.updatePost(postId, updatePayload));

        Assertions.assertEquals("Post not found", result.getMessage());
    }

    @Test
    public void testUpdatePost() {
        long postId = 1L;
        Post existingPost = new Post();
        existingPost.setId(postId);
        existingPost.setTitle("Old Title");

        UpdatePostPayload updatePayload = new UpdatePostPayload();
        updatePayload.setTitle("New Title");
        updatePayload.setContent("Updated Content");
        updatePayload.setDescription("Updated Description");
        updatePayload.setLogo("https://updated.logo.com");

        User user = new User();
        user.setUsername("UpdatedUser");

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));
        when(securityService.getUser()).thenReturn(user);
        when(postRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Post result = postService.updatePost(postId, updatePayload);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("New Title", result.getTitle());
        Assertions.assertEquals("Updated Content", result.getContent());
        Assertions.assertEquals("Updated Description", result.getDescription());
        Assertions.assertEquals("https://updated.logo.com", result.getLogo());
        Assertions.assertEquals("UpdatedUser", result.getAuthor());
    }

    @Test
    public void testDeletePostSuccess() {
        long postId = 1L;

        when(postRepository.existsById(postId)).thenReturn(true);

        postService.deletePost(postId);

        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    public void testDeletePostWhenPostNotFound() {
        long postId = 1L;

        when(postRepository.existsById(postId)).thenReturn(false);

        Throwable result = assertThrows(EntityNotFoundException.class, () -> postService.deletePost(postId));
        assertEquals("Post not found with id: " + postId, result.getMessage());

        verify(postRepository, never()).deleteById(anyLong());
    }


}

