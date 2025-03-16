//package com.example.Blog.Project.integration;
//
//import static org.hamcrest.Matchers.is;
//
//import com.example.Blog.Project.category.model.Category;
//import com.example.Blog.Project.category.repository.CategoryRepository;
//import com.example.Blog.Project.listeners.DataRemovalListener;
//import com.example.Blog.Project.post.model.Post;
//import com.example.Blog.Project.web.controller.PostController;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.TestExecutionListeners;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Set;
//
//import static org.hamcrest.Matchers.notNullValue;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@RunWith(SpringRunner.class)
//@TestExecutionListeners(
//    listeners = DataRemovalListener.class,
//    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
//)
//public class PostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private PostController postController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Test
//    @WithMockUser(username = "dummy", authorities = { "p.u", "p.d", "p.r" })
//    public void testCreatePostWithoutPostCreatePermission() {
//        try {
//            this.mockMvc.perform(post("/posts/add").contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
//                    .andExpect(status().isForbidden());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    public void testCreatePostWithoutAuthentication() {
//        try {
//            this.mockMvc.perform(post("/posts/add").contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
//                    .andExpect(status().isForbidden());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    @WithMockUser(username = "dummy", authorities = { "p.c" })
//    public void testCreatePostWithoutPayload() {
//        try {
//            this.mockMvc.perform(post("/posts/add").contentType(MediaType.APPLICATION_JSON))
//                    .andDo(print())
//                    .andExpect(status().isBadRequest());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Test
//    @WithMockUser(username = "dummy", authorities = { "p.c" })
//    public void testCreatePost() throws JsonProcessingException {
//        Post postPayload = new Post();
//        postPayload.setTitle("post-1");
//        postPayload.setContent("Dummy content");
//        postPayload.setLogo("https://dummy.com/123");
//        postPayload.setDescription("Dummy");
//        Category category = new Category();
//        category.setId(1L);
//        postPayload.setCategories(Set.of(category));
//
//        String jsonData = objectMapper.writeValueAsString(postPayload);
//
//        try {
//            this.mockMvc.perform(post("/posts/add").content(jsonData).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$.title", is(postPayload.getTitle())))
//                    .andExpect(jsonPath("$.description", is(postPayload.getDescription())))
//                    .andExpect(jsonPath("$.author", is("dummy")))
//                    .andExpect(jsonPath("$.content", is(postPayload.getContent())))
//                    .andExpect(jsonPath("$.logo", is(postPayload.getLogo())))
//                    .andExpect(jsonPath("$.createdAt", notNullValue()));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//}
