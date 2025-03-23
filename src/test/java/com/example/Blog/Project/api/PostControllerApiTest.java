package com.example.Blog.Project.api;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.service.PostService;
import com.example.Blog.Project.web.controller.PostController;
import com.example.Blog.Project.web.dto.AddPostPayload;
import com.example.Blog.Project.web.dto.UpdatePostPayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerApiTest extends BaseApiTest{

    @MockitoBean
    private PostService postService;

    @Test
    void testGetAllPostsWhenNotAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/posts");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"p.c", "r.c"})
    void testGetAllPostsWhenAuthenticatedButNoPermissions() throws Exception {
        MockHttpServletRequestBuilder request = get("/posts");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.r"})
    void testGetAll_ShouldReturnPosts_WhenNoCategoryIdProvided() throws Exception {

        MockHttpServletRequestBuilder request = get("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Category category = new Category();
        category.setTitle("Technology");

        Post post1 = new Post();
        post1.setCategories(Set.of(category));
        post1.setAuthor("dummy");
        post1.setDescription("new Technology");
        post1.setContent("Technology");
        post1.setLogo("technologyLogo");
        post1.setTitle("Technology");

        Post post2 = new Post();
        post2.setCategories(Set.of(category));
        post2.setAuthor("dummy2");
        post2.setDescription("Another technology post");
        post2.setContent("More Technology");
        post2.setLogo("anotherTechnologyLogo");
        post2.setTitle("Another Tech");

        List<Post> posts = List.of(post1, post2);
        Page<Post> page = new PageImpl<>(posts, PageRequest.of(0, 20), posts.size());

        when(postService.getAll(Mockito.any(), eq(null))).thenReturn(page);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(post1.getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(post2.getTitle())))
                .andExpect(jsonPath("$.totalElements", is(posts.size())))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(postService, times(1)).getAll(Mockito.any(), eq(null));

    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.r"})
    void testGetAll_ShouldReturnPosts_WhenCategoryIdIsProvided() throws Exception {

        MockHttpServletRequestBuilder request = get("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("categoryId", "1");

        Category category = new Category();
        category.setId(1L);
        category.setTitle("Technology");

        Post post1 = new Post();
        post1.setCategories(Set.of(category));
        post1.setAuthor("dummy");
        post1.setDescription("new Technology");
        post1.setContent("Technology");
        post1.setLogo("technologyLogo");
        post1.setTitle("Technology");

        Post post2 = new Post();
        post2.setCategories(Set.of(category));
        post2.setAuthor("dummy2");
        post2.setDescription("Another technology post");
        post2.setContent("More Technology");
        post2.setLogo("anotherTechnologyLogo");
        post2.setTitle("Another Tech");

        List<Post> posts = List.of(post1, post2);
        Page<Post> page = new PageImpl<>(posts, PageRequest.of(0, 20), posts.size());

        when(postService.getAll(Mockito.any(), eq(1L))).thenReturn(page);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].title", is(post1.getTitle())))
                .andExpect(jsonPath("$.content[1].title", is(post2.getTitle())))
                .andExpect(jsonPath("$.totalElements", is(posts.size())))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(postService, times(1)).getAll(Mockito.any(), eq(1L));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.r"})
    void testGetAllPosts_ShouldReturnBadRequest_WhenCategoryIdIsInvalid() throws Exception {

        String invalidCategoryId = "invalidCategory";

        MockHttpServletRequestBuilder request = get("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("categoryId", invalidCategoryId);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());

        verify(postService, never()).getAll(Mockito.any(), eq(null));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.r"})
    void testGetPostById_ShouldReturnPost_WhenPostExists() throws Exception {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("Sample Post");

        when(postService.findPost(postId)).thenReturn(post);

        mockMvc.perform(get("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Sample Post")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.r"})
    void testGetPostById_ShouldReturnNotFound_WhenPostDoesNotExist() throws Exception {
        long postId = 999L;

        when(postService.findPost(postId))
                .thenThrow(new EntityNotFoundException("Post not found"));

        mockMvc.perform(get("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.r"})
    void testGetPostById_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/posts/{id}", "abc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPostById_ShouldReturnForbidden_WhenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"c.r"})
    void testGetPostById_ShouldReturnForbidden_WhenUserLacksPermission() throws Exception {
        mockMvc.perform(get("/posts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.c"})
    void testCreatePost_ShouldReturnCreatedPost_WhenDataIsValid() throws Exception {
        AddPostPayload payload = new AddPostPayload();
        payload.setTitle("New Post");
        payload.setContent("Content of the post");
        payload.setDescription("Post description");
        payload.setLogo("https://image.com/logo.jpg");

        Category category = new Category();
        category.setId(1L);
        category.setTitle("Tech");
        payload.setCategories(Set.of(category));

        Post createdPost = new Post();
        createdPost.setId(1L);
        createdPost.setTitle(payload.getTitle());
        createdPost.setContent(payload.getContent());
        createdPost.setDescription(payload.getDescription());
        createdPost.setLogo(payload.getLogo());
        createdPost.setCategories(payload.getCategories());

        when(postService.createPost(any(AddPostPayload.class))).thenReturn(createdPost);

        mockMvc.perform(post("/posts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("New Post")))
                .andExpect(jsonPath("$.content", is("Content of the post")))
                .andExpect(jsonPath("$.description", is("Post description")))
                .andExpect(jsonPath("$.logo", is("https://image.com/logo.jpg")))
                .andExpect(jsonPath("$.categories[0].title", is("Tech")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.c"})
    void testCreatePost_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        AddPostPayload invalidPayload = new AddPostPayload();
        invalidPayload.setTitle("");
        invalidPayload.setDescription("Valid Description");
        invalidPayload.setContent("Valid Content");
        invalidPayload.setLogo("https://image.com/logo.jpg");
        invalidPayload.setCategories(null);

        mockMvc.perform(post("/posts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreatePost_ShouldReturnForbidden_WhenUserNotAuthenticated() throws Exception {
        AddPostPayload payload = new AddPostPayload();
        payload.setTitle("New Post");

        mockMvc.perform(post("/posts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"p.r"})
    void testCreatePost_ShouldReturnForbidden_WhenUserLacksPermission() throws Exception {
        AddPostPayload payload = new AddPostPayload();
        payload.setTitle("New Post");

        mockMvc.perform(post("/posts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.c"})
    void testCreatePost_ShouldReturnException_WhenPostAlreadyExists() throws Exception {

        AddPostPayload addPostPayload = new AddPostPayload();
        addPostPayload.setTitle("Existing Post Title");

        when(postService.createPost(any(AddPostPayload.class)))
                .thenThrow(new IllegalArgumentException("Post title already exist!"));

        mockMvc.perform(post("/posts/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addPostPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.u"})
    void testUpdatePost_ShouldReturnUpdatedPost_WhenDataIsValid() throws Exception {
        long postId = 1L;

        Category category = new Category();
        category.setId(1L);
        category.setTitle("Updated Category");

        UpdatePostPayload updatePostPayload = new UpdatePostPayload();
        updatePostPayload.setTitle("Updated Title");
        updatePostPayload.setContent("Updated Content");
        updatePostPayload.setDescription("Updated Description");
        updatePostPayload.setLogo("Updated Logo");
        updatePostPayload.setCategories(Set.of(category));

        Post updatedPost = new Post();
        updatedPost.setId(postId);
        updatedPost.setTitle(updatePostPayload.getTitle());
        updatedPost.setContent(updatePostPayload.getContent());
        updatedPost.setDescription(updatePostPayload.getDescription());
        updatedPost.setLogo(updatePostPayload.getLogo());
        updatedPost.setCategories(Set.of(category));

        when(postService.updatePost(eq(postId),any(UpdatePostPayload.class))).thenReturn(updatedPost);

        mockMvc.perform(put("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.content", is("Updated Content")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.u"})
    void testUpdatePost_ShouldReturnNotFound_WhenPostDoesNotExist() throws Exception {
        long postId = 999L;

        Category category = new Category();
        category.setTitle("Category");

        UpdatePostPayload updatePostPayload = new UpdatePostPayload();
        updatePostPayload.setTitle("Updated Title");
        updatePostPayload.setContent("Updated Content");
        updatePostPayload.setDescription("UpdatedDescription");
        updatePostPayload.setLogo("UpdatedLogo");
        updatePostPayload.setCategories(Set.of(category));


        when(postService.updatePost(eq(postId), any(UpdatePostPayload.class)))
                .thenThrow(new EntityNotFoundException("Post not found"));

        mockMvc.perform(put("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostPayload)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.u"})
    void testUpdatePost_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        long postId = 1L;
        UpdatePostPayload updatePostPayload = new UpdatePostPayload();
        updatePostPayload.setTitle("");
        updatePostPayload.setContent("Valid content");

        mockMvc.perform(put("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"p.r"})
    void testUpdatePost_ShouldReturnForbidden_WhenUserLacksPermission() throws Exception {
        long postId = 1L;
        UpdatePostPayload updatePostPayload = new UpdatePostPayload();
        updatePostPayload.setTitle("Updated Title");
        updatePostPayload.setContent("Updated Content");

        mockMvc.perform(put("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostPayload)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdatePost_ShouldReturnForbidden_WhenUserIsNotAuthenticated() throws Exception {
        long postId = 1L;
        UpdatePostPayload updatePostPayload = new UpdatePostPayload();
        updatePostPayload.setTitle("Updated Title");
        updatePostPayload.setContent("Updated Content");

        mockMvc.perform(put("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePostPayload)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.d"})
    public void testDeletePost_Success() throws Exception {
        long postId = 1L;

        doNothing().when(postService).deletePost(postId);

        mockMvc.perform(delete("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""))
                .andReturn();

        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.d"})
    public void testDeletePost_PostNotFound() throws Exception {
        long postId = 999L;

        doThrow(new EntityNotFoundException("Post not found")).when(postService).deletePost(postId);

        mockMvc.perform(delete("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(postService, times(1)).deletePost(postId);
    }

    @Test
    @WithMockUser(username = "User", authorities = {"p.r"})
    public void testDeletePost_Forbidden() throws Exception {
        long postId = 1L;

        mockMvc.perform(delete("/posts/{id}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();

        verify(postService, never()).deletePost(postId);
    }
}
