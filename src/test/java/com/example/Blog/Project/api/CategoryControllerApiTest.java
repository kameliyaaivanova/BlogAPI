package com.example.Blog.Project.api;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.category.service.CategoryService;
import com.example.Blog.Project.web.controller.CategoryController;
import com.example.Blog.Project.web.dto.AddCategoryPayload;
import com.example.Blog.Project.web.dto.UpdateCategoryPayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerApiTest extends BaseApiTest{

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void testGetAllCategoriesWhenNotAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/categories");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"r.r", "r.c"})
    void testGetAllCategoriesWhenAuthenticatedButNoPermissions() throws Exception {
        MockHttpServletRequestBuilder request = get("/categories");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"r.r", "r.c", "c.r"})
    void testGetAllCategoriesWhenAuthenticatedWithSufficientPermissionsButNoCategories() throws Exception {
        MockHttpServletRequestBuilder request = get("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        when(categoryService.getAll(Mockito.any())).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content", empty()))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"c.r"})
    void testGetAllCategoriesWhenAuthenticatedWithSufficientPermissions() throws Exception {

        MockHttpServletRequestBuilder request = get("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Category category = new Category();
        category.setTitle("category1");
        category.setId(1);

        when(categoryService.getAll(Mockito.any())).thenReturn(new PageImpl<>(List.of(category), PageRequest.of(0, 20), 1));

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content[0].title", is(category.getTitle())))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"c.r"})
    void testGetCategoryById_ShouldReturnCategory_WhenCategoryExists() throws Exception {
        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setTitle("category1");

        when(categoryService.findById(1L)).thenReturn(testCategory);

        mockMvc.perform(get("/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("category1")));

        verify(categoryService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"c.r"})
    void testGetCategoryById_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        long categoryId = 999L;

        when(categoryService.findById(categoryId)).thenThrow(new EntityNotFoundException("Category not found"));

        mockMvc.perform(get("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"c.u"})
    void testUpdateCategory_ShouldReturnUpdatedCategory_WhenDataIsValid() throws Exception {
        long categoryId = 1L;

        UpdateCategoryPayload updatePayload = new UpdateCategoryPayload("category_updated");

        Category updatedCategory = new Category();
        updatedCategory.setId(categoryId);
        updatedCategory.setTitle(updatePayload.getTitle());

        when(categoryService.update(eq(categoryId), any(UpdateCategoryPayload.class))).thenReturn(updatedCategory);

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("category_updated")));

        verify(categoryService, times(1)).update(eq(categoryId), any(UpdateCategoryPayload.class));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"c.u"})
    void testUpdateCategory_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        long categoryId = 999L;

        UpdateCategoryPayload updatePayload = new UpdateCategoryPayload("Updated Category");

        when(categoryService.update(eq(categoryId), any(UpdateCategoryPayload.class)))
                .thenThrow(new EntityNotFoundException("Category not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isNotFound());

        verify(categoryService, times(1)).update(eq(categoryId), any(UpdateCategoryPayload.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"c.u"})
    void testUpdateCategory_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        long categoryId = 1L;

        UpdateCategoryPayload invalidPayload = new UpdateCategoryPayload("");

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest());

        verify(categoryService, times(0)).update(eq(categoryId), any(UpdateCategoryPayload.class));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"c.r"})
    void testUpdateCategory_ShouldReturnForbidden_WhenUserLacksPermission() throws Exception {
        long categoryId = 1L;
        UpdateCategoryPayload updatePayload = new UpdateCategoryPayload("Updated Electronics");

        mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).update(anyLong(), any(UpdateCategoryPayload.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"c.c"})
    void testCreateCategory_ShouldReturnCreatedCategory_WhenValidDataProvided() throws Exception {
        AddCategoryPayload validCategory = new AddCategoryPayload("Technology");

        Category createdCategory = new Category();
        createdCategory.setTitle("Technology");
        createdCategory.setId(1L);

        when(categoryService.create(any(AddCategoryPayload.class))).thenReturn(createdCategory);

        mockMvc.perform(post("/categories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Technology")));

        verify(categoryService, times(1)).create(any(AddCategoryPayload.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"c.c"})
    void testCreateCategory_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {

        AddCategoryPayload invalidCategory = new AddCategoryPayload();
        invalidCategory.setTitle("");

                mockMvc.perform(post("/categories/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(AddCategoryPayload.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"c.c","c.r"})
    void testCreateCategory_ShouldReturnConflict_WhenTitleAlreadyExists() throws Exception {
        AddCategoryPayload categoryPayload = new AddCategoryPayload();
        categoryPayload.setTitle("Technology");

        when(categoryService.create(any()))
                .thenThrow(new IllegalArgumentException("Category already exists!"));

        mockMvc.perform(post("/categories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryPayload)))
                .andExpect(status().isConflict());

   }

    @Test
    @WithMockUser(username = "user", authorities = {"c.r"})
    void testCreateCategory_ShouldReturnForbidden_WhenUserNotAuthorized() throws Exception {
        AddCategoryPayload validCategory = new AddCategoryPayload("Science");

        mockMvc.perform(post("/categories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCategory)))
                .andExpect(status().isForbidden());

        verify(categoryService, never()).create(any(AddCategoryPayload.class));
    }

    @Test
    void testCreateCategory_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {
        AddCategoryPayload validCategory = new AddCategoryPayload("Science");

        mockMvc.perform(post("/categories/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCategory)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        verify(categoryService, never()).create(any(AddCategoryPayload.class));
    }
}
