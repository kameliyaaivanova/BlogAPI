package com.example.Blog.Project.api;

import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.service.UserService;
import com.example.Blog.Project.web.controller.UserController;
import com.example.Blog.Project.web.dto.RegisterPayload;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = UserController.class)
class UserControllerApiTest extends BaseApiTest {

    @MockitoBean
    private UserService userService;

    @Test
    void testGetAllUsersWhenNotAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/users");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "r.r", "r.c" })
    void testGetAllUsersWhenAuthenticatedButNoPermissions() throws Exception {
        MockHttpServletRequestBuilder request = get("/users");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "r.r", "r.c", "u.r" })
    void testGetAllUsersWhenAuthenticatedWithSufficientPermissionsButNoUsers() throws Exception {
        MockHttpServletRequestBuilder request = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        when(userService.getAll(Mockito.any())).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content", empty()))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "r.r", "r.c", "u.r" })
    void testGetAllUsersWhenAuthenticatedWithSufficientPermissions() throws Exception {
        MockHttpServletRequestBuilder request = get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        User user = new User();
        user.setUsername("u1");
        user.setEmail("u1@gmail.com");
        when(userService.getAll(Mockito.any())).thenReturn(new PageImpl<>(List.of(user), PageRequest.of(0, 20), 1));

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$.content[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "u.r" })
    void testGetUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");

        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.id", is(1))) // Validate ID
                .andExpect(jsonPath("$.username", is("john_doe"))) // Validate username
                .andExpect(jsonPath("$.email", is("john@example.com"))); // Validate email

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "u.r" })
    void testGetUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.findById(999L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Expect HTTP 404
                .andExpect(content().string("User not found"));

        verify(userService, times(1)).findById(999L);
    }

    @Test
    void testRegister_ShouldReturnNoContent_WhenRegistrationSuccessful() throws Exception {
        RegisterPayload payload = new RegisterPayload();
        payload.setUsername("testUser");
        payload.setEmail("test@example.com");
        payload.setPassword("SecurePass123!");

        when(userService.register(payload)).thenReturn(true);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))  // Converts object to JSON
                .andExpect(status().isNoContent());
    }

    @Test
    void testRegister_ShouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        RegisterPayload registerPayload = new RegisterPayload();
        registerPayload.setUsername("kamkam");
        registerPayload.setEmail("kamkam@abv.bg");
        registerPayload.setPassword("Kamkam123!");

        String jsonPayload = objectMapper.writeValueAsString(registerPayload);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict());
    }

    @Test
    void testRegister_ShouldReturnBadRequest_WhenPayloadIsInvalid() throws Exception {
        RegisterPayload registerPayload = new RegisterPayload();
        registerPayload.setUsername("");
        registerPayload.setEmail("invalid-email");
        registerPayload.setPassword("");

        String jsonPayload = objectMapper.writeValueAsString(registerPayload);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"u.r"})
    void testGetAllUsers_ShouldReturnPageOfUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john_doe");
        user1.setEmail("john@example.com");

        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("jan_doe");
        user2.setEmail("jan@example.com");

        List<User> users = List.of(user1,user2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userService.getAll(pageable)).thenReturn(userPage);

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username", is("john_doe")))
                .andExpect(jsonPath("$.content[1].username", is("jan_doe")))
                .andExpect(jsonPath("$.totalElements", is(2)))  // Ensure totalElements = 2
                .andExpect(jsonPath("$.totalPages", is(1)));;
    }


}
