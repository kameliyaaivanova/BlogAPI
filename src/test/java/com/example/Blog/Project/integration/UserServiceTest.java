package com.example.Blog.Project.integration;

import java.time.Instant;
import java.util.List;

import com.example.Blog.Project.exception.AuthenticationFailedException;
import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.permission.model.PermissionOption;
import com.example.Blog.Project.refreshtoken.model.RefreshToken;
import com.example.Blog.Project.refreshtoken.repository.RefreshTokenRepository;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.security.JwtService;
import com.example.Blog.Project.user.model.AuthResponse;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.repository.UserRepository;
import com.example.Blog.Project.user.service.UserService;
import com.example.Blog.Project.web.dto.AddUserPayload;
import com.example.Blog.Project.web.dto.LoginPayload;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetAll() {
        Assertions.assertTrue(userService.getAll(Pageable.ofSize(20)).getContent().isEmpty());
    }

    @Test
    public void testGetAllWhenThereAreUsers() {
        List<Permission> permissions = seeder.permissions(PermissionOption.READ_POSTS, PermissionOption.CREATE_POSTS);
        Role role = seeder.role("User", permissions);
        seeder.user("dummy", "dummy@gmail.com", "Test123", role);

        Page<User> usersPage = userService.getAll(Pageable.ofSize(20));
        User user = usersPage.getContent().get(0);

        Assertions.assertEquals(1, usersPage.getTotalElements());
        Assertions.assertNotNull(user.getRole());
    }

    @Test
    public void testCreateUser() {
        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
        Role role = seeder.role("Admin", permissions);
        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("dummy");
        addUserPayload.setPassword("Test123456");
        addUserPayload.setEmail("dummy@gmail.com");
        addUserPayload.setRole(role);

        User createdUser = userService.create(addUserPayload);

        Assertions.assertNotNull(createdUser);
        Assertions.assertTrue(userRepository.existsByUsernameOrEmail(addUserPayload.getUsername(), addUserPayload.getEmail()));
        Assertions.assertEquals(addUserPayload.getUsername(), createdUser.getUsername());
        Assertions.assertEquals(addUserPayload.getEmail(), createdUser.getEmail());
        Assertions.assertNotEquals(addUserPayload.getPassword(), createdUser.getPassword());
        Assertions.assertNotNull(createdUser.getRole());
    }

    @Test
    public void testCreateUserWhenUsernameAlreadyExists() {
        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
        Role role = seeder.role("Admin", permissions);
        User user = seeder.user("dummy", "dummy@gmail.com", "Test123", role);

        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("dummy");

        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.create(addUserPayload));
    }

    @Test
    public void testCreateUserWhenEmailAlreadyExists() {
        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
        Role role = seeder.role("Admin", permissions);
        User user = seeder.user("dummy", "dummy@gmail.com", "Test123", role);

        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("dummy@gmail.com");

        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.create(addUserPayload));
    }

    @Test
    public void testFindByIdWhenItDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    public void testFindById() {
        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
        Role role = seeder.role("Admin", permissions);
        User user = seeder.user("dummy", "dummy@gmail.com", "Test123", role);

        Assertions.assertNotNull(userService.findById(user.getId()));
    }

    @Test
    public void testDeleteById() {
        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
        Role role = seeder.role("Admin", permissions);
        User user = seeder.user("dummy", "dummy@gmail.com", "Test123", role);

        Assertions.assertTrue(userRepository.existsById(user.getId()));

        userService.delete(user.getId());

        Assertions.assertFalse(userRepository.existsById(user.getId()));
    }

    @Test
    public void testDeleteByIdWhenUserDoesNotExist() {
        Assertions.assertEquals(0, userRepository.count());

        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.delete(1L));
    }

    @Test
    public void testAuthenticateWhenRefreshTokenDoesNotExistInDatabase() {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setRefreshToken("dummy-refreshtoken");

        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "dummy"));
    }

//    @Test
//    public void testAuthenticateWhenOldRefreshTokenExistInDatabase() {
//        LoginPayload loginPayload = new LoginPayload();
//        loginPayload.setRefreshToken("dummy-refreshtoken");
//
//        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
//        Role role = seeder.role("Admin", permissions);
//        User user = seeder.user("dummy", "dummy@gmail.com", "Test123", role);
//
//        user = userRepository.findById(user.getId()).get();
//
//        RefreshToken oldRefreshToken = seeder.refreshTokenTest("dummy-old-token", Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY), user, null);
//        oldRefreshToken = refreshTokenRepository.save(oldRefreshToken);
//
//        oldRefreshToken = refreshTokenRepository.findById(oldRefreshToken.getId()).get();
//
//        RefreshToken refreshToken = seeder.refreshTokenTest(loginPayload.getRefreshToken(), Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY), user, oldRefreshToken.getToken());
//        refreshTokenRepository.save(refreshToken);
//
//        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "dummy"));
//    }

    @Test
    @Transactional
    public void testAuthenticateWhenRefreshTokenExistInDatabase() {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setRefreshToken("dummy-token");

        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
        Role role = seeder.role("Admin", permissions);
        User user = seeder.user("dummy", "dummy@gmail.com", "Test123", role);

        seeder.refreshToken(loginPayload.getRefreshToken(), Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY), user, null);

        AuthResponse authResponse = userService.authenticate(loginPayload, "user-agent");
        Assertions.assertNotNull(authResponse.getToken());
        Assertions.assertNotNull(authResponse.getRefreshToken());

        // TODO check that the refresh token is renewed
    }

    @Test
    @Transactional
    public void testAuthenticateWhenUserByUsernameDoesNotExist() {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("dummy");
        loginPayload.setPassword("Test123");

        Throwable throwable = Assertions.assertThrows(EntityNotFoundException.class, () -> userService.authenticate(loginPayload, "user-agent"));
        Assertions.assertEquals(String.format("User with username: %s was not found.", loginPayload.getUsername()), throwable.getMessage());
    }

    @Test
    @Transactional
    public void testAuthenticateWhenUserHasDifferentPassword() {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("dummy");
        loginPayload.setPassword("Test123");

        List<Permission> permissions = seeder.permissions(PermissionOption.CREATE_POSTS, PermissionOption.READ_POSTS);
        Role role = seeder.role("Admin", permissions);
        User user = seeder.user("dummy", "dummy@gmail.com", "Test1234", role);

        Throwable throwable = Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "user-agent"));
        Assertions.assertEquals("Invalid credentials", throwable.getMessage());
    }
}
