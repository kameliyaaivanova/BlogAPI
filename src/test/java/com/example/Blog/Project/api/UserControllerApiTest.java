package com.example.Blog.Project.api;

import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.user.model.AuthResponse;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.service.UserService;
import com.example.Blog.Project.web.controller.UserController;
import com.example.Blog.Project.web.dto.AddUserPayload;
import com.example.Blog.Project.web.dto.LoginPayload;
import com.example.Blog.Project.web.dto.RegisterPayload;
import com.example.Blog.Project.web.dto.UpdateUserPayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @WithMockUser(username = "dummy", authorities = {"r.r", "r.c"})
    void testGetAllUsersWhenAuthenticatedButNoPermissions() throws Exception {
        MockHttpServletRequestBuilder request = get("/users");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"r.r", "r.c", "u.r"})
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
    @WithMockUser(username = "dummy", authorities = {"r.r", "r.c", "u.r"})
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
    @WithMockUser(username = "dummy", authorities = {"u.r"})
    void testGetUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");
        testUser.setEmail("john@example.com");

        when(userService.findById(1L)).thenReturn(testUser);

        mockMvc.perform(get("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john_doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"u.r"})
    void testGetUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.findById(999L)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
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
                        .content(objectMapper.writeValueAsString(payload)))
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

        List<User> users = List.of(user1, user2);

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
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
        ;
    }

    @Test
    void testLogin_ShouldReturnAuthResponse_WhenCredentialsAreValid() throws Exception {
        LoginPayload payload = new LoginPayload();
        payload.setUsername("testUser");
        payload.setPassword("SecurePass123!");
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("mock-jwt-token");

        when(userService.authenticate(payload, "TestUserAgent")).thenReturn(authResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .header("User-Agent", "TestUserAgent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    void testLogin_ShouldReturnBadRequest_WhenMissingUsernameOrPassword() throws Exception {
        LoginPayload payload = new LoginPayload();
        payload.setUsername("");
        payload.setPassword("SecurePass123!");

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .header("User-Agent", "TestUserAgent"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "Admin",authorities = {"u.r"})
    void testGetById_ShouldReturnUser_WhenUserExistsAndIsAuthorised() throws Exception {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        MockHttpServletRequestBuilder request = get("/users/{id}",userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON);

        when(userService.findById(userId)).thenReturn(user);

        mockMvc.perform(request)
                .andExpect(jsonPath("$.username", is("testUser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"u.r", "u.u"})
    void testUpdateUser_ShouldReturnUpdatedUser_WhenUserExists() throws Exception {
        long userId = 1L;

        Role role = new Role();
        Permission permission = new Permission();
        permission.setDescription("to create");
        permission.setTitle("Creator");
        role.setName("Creator");
        role.setPermissions(Set.of(permission));

        UpdateUserPayload updateUserPayload = new UpdateUserPayload();
        updateUserPayload.setUsername("john_doe_updated");
        updateUserPayload.setEmail("john_updated@example.com");
        updateUserPayload.setRole(role);
        updateUserPayload.setPassword("JohnDoe123Updated!");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername(updateUserPayload.getUsername());
        updatedUser.setEmail(updateUserPayload.getEmail());
        updatedUser.setRole(role);
        updatedUser.setPassword(updateUserPayload.getPassword());


        when(userService.update(eq(userId),any(UpdateUserPayload.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john_doe_updated")))
                .andExpect(jsonPath("$.email", is("john_updated@example.com")));

        verify(userService, times(1)).update(eq(userId), any(UpdateUserPayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"u.r", "u.u"})
    void testUpdateUser_ShouldReturnNotFoundException_WhenUserDoesNotExist() throws Exception {
        long userId = 999L;

        Role role = new Role();
        Permission permission = new Permission();
        permission.setDescription("to create");
        permission.setTitle("Creator");
        role.setName("Creator");
        role.setPermissions(Set.of(permission));

        UpdateUserPayload updateUserPayload = new UpdateUserPayload();
        updateUserPayload.setUsername("non_existent_user");
        updateUserPayload.setEmail("nonexistent@example.com");
        updateUserPayload.setRole(role);
        updateUserPayload.setPassword("NonExistingPassword123!");

        when(userService.update(eq(userId), any(UpdateUserPayload.class)))
                .thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserPayload)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).update(eq(userId), any(UpdateUserPayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"u.r", "u.u"})
    void testUpdateUser_ShouldReturnBadRequest_WhenInvalidDataIsProvided() throws Exception {
        long userId = 1L;

        UpdateUserPayload updateUserPayload = new UpdateUserPayload();
        updateUserPayload.setUsername("");
        updateUserPayload.setEmail("invalid_email_format");

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserPayload)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).update(eq(userId), any(UpdateUserPayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"u.r"})
    void testUpdateUser_ShouldReturnForbidden_WhenUserLacksPermissions() throws Exception {
        long userId = 1L;

        UpdateUserPayload updateUserPayload = new UpdateUserPayload();
        updateUserPayload.setUsername("restricted_user");
        updateUserPayload.setEmail("restricted@example.com");

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserPayload)))
                .andExpect(status().isForbidden());

        verify(userService, times(0)).update(eq(userId), any(UpdateUserPayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"u.c", "u.u","u.r"})
    void testCreateUser_ShouldReturnCreatedUser_WhenValidDataProvided() throws Exception {

        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("john_doe");
        addUserPayload.setEmail("john@example.com");
        addUserPayload.setPassword("Password123!");
        Role role = new Role();
        Permission permission1 = new Permission();
        permission1.setTitle("Create user");
        permission1.setDescription("ability to create new users");
        Permission permission2 = new Permission();
        permission2.setTitle("Read user");
        permission2.setDescription("ability to read all users");
        role.setPermissions(Set.of(permission1,permission2));
        role.setName("Admin");
        addUserPayload.setRole(role);

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername("john_doe");
        createdUser.setEmail("john@example.com");

        when(userService.create(any(AddUserPayload.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addUserPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john_doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(userService, times(1)).create(any(AddUserPayload.class));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"u.c"})
    void testCreateUser_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {

        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("");
        addUserPayload.setEmail("invalidemail.com");
        addUserPayload.setPassword("Password123!");

        mockMvc.perform(post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addUserPayload)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).create(any(AddUserPayload.class));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"u.c"})
    void testCreateUser_ShouldReturnException_WhenUserAlreadyExists() throws Exception {

        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("john_doe");
        addUserPayload.setEmail("john@example.com");
        addUserPayload.setPassword("Password123!");

        when(userService.create(any(AddUserPayload.class))).thenThrow(new IllegalArgumentException("User with given username or email already exists"));

        mockMvc.perform(post("/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addUserPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"u.r"})
    void testCreateUser_ShouldReturnForbidden_WhenUserLacksPermissions() throws Exception {
        long userId = 1L;

        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("john_doe");
        addUserPayload.setEmail("john@example.com");
        addUserPayload.setPassword("Password123!");

        mockMvc.perform(post("/users/add", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addUserPayload)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"u.d"})
    void testDeleteUser_ShouldReturnNoContent_WhenUserExists() throws Exception {
        long userId = 1L;

        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"u.d"})
    void testDeleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        long userId = 999L;

        doThrow(new EntityNotFoundException("User not found")).when(userService).delete(userId);

        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Admin",authorities = {"u.r"})
    void testDeleteUser_ShouldReturnForbidden_WhenUserHaveNoPermissions() throws Exception {
        long userId = 1L;

        mockMvc.perform(delete("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
