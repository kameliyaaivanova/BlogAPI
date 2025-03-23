package com.example.Blog.Project.integration;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.example.Blog.Project.web.dto.RegisterPayload;
import com.example.Blog.Project.web.dto.UpdateUserPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class UserServiceTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Test
    public void testGetAllWhenNoUsers() {
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
    public void testLoadUserByUsername_UserExists_ShouldReturnUserDetails() {

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setEnabled(true);
        userRepository.save(user);

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound_ShouldThrowException() {

        assertThrows(EntityNotFoundException.class, () -> userService.loadUserByUsername("unknownUser"));
    }

    @Test
    public void testRegister_NewUser_ShouldRegisterSuccessfully() {

        List<Permission> permissions = seeder.permissions(PermissionOption.READ_POSTS, PermissionOption.CREATE_POSTS);
        Role role = seeder.role("User", permissions);

        RegisterPayload payload = new RegisterPayload();
        payload.setUsername("newUser");
        payload.setEmail("newUser@example.com");
        payload.setPassword("securePassword");

        boolean result = userService.register(payload);

        assertTrue(result);
        assertTrue(userRepository.existsByUsernameOrEmail("newUser", "newUser@example.com"));
    }

    @Test
    public void testRegister_ExistingUsernameOrEmail_ShouldThrowException() {

        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("password");
        existingUser.setEnabled(true);
        userRepository.save(existingUser);

        RegisterPayload payload = new RegisterPayload();
        payload.setUsername("existingUser");
        payload.setEmail("existing@example.com");
        payload.setPassword("anotherPassword");

        assertThrows(IllegalArgumentException.class, () -> userService.register(payload));
    }

    @Test
    void updateUser_SuccessfulUpdateAllFields() {

        List<Permission> permissions = seeder.permissions(PermissionOption.READ_POSTS, PermissionOption.CREATE_POSTS);
        Role role = seeder.role("User", permissions);

        User user = new User();
        user.setPassword("oldPassword");
        user.setRole(role);
        user.setUsername("username");
        user.setEmail("useremail@abv.bg");
        userRepository.save(user);

        UpdateUserPayload payload = new UpdateUserPayload();
        payload.setUsername("newRole");
        payload.setPassword("newPass123!");
        payload.setEmail("newemail@example.com");
        payload.setRole(role);

        User updatedUser = userService.update(user.getId(), payload);

        assertEquals(payload.getRole(), updatedUser.getRole());
        assertTrue(passwordEncoder.matches(payload.getPassword(), updatedUser.getPassword()));
        assertEquals(payload.getEmail(), updatedUser.getEmail());
        assertEquals(payload.getUsername(), updatedUser.getUsername());
    }

    @Test
    void updateUser_UserNotFound_ShouldThrowException() {
        List<Permission> permissions = seeder.permissions(PermissionOption.READ_POSTS, PermissionOption.CREATE_POSTS);
        Role role = seeder.role("User", permissions);

        UpdateUserPayload payload = new UpdateUserPayload();
        payload.setUsername("newRole");
        payload.setPassword("newPass123!");
        payload.setEmail("newemail@example.com");
        payload.setRole(role);

        assertThrows(EntityNotFoundException.class, () -> userService.update(999L, payload));
    }

    @Test
    void updateUser_UpdateOnlyPassword_ShouldEncodePassword() {

        List<Permission> permissions = seeder.permissions(PermissionOption.READ_POSTS, PermissionOption.CREATE_POSTS);
        Role role = seeder.role("User", permissions);

        User user = new User();
        user.setPassword("oldPassword");
        user.setRole(role);
        user.setUsername("username");
        user.setEmail("useremail@abv.bg");
        userRepository.save(user);

        UpdateUserPayload payload = new UpdateUserPayload();
        payload.setUsername(user.getUsername());
        payload.setEmail(user.getEmail());
        payload.setRole(user.getRole());
        payload.setPassword("newSecret123!");
        User updatedUser = userService.update(user.getId(), payload);

        assertTrue(passwordEncoder.matches("newSecret123!", updatedUser.getPassword()));
    }

    @Test
    void findByUsername_UserExists_ShouldReturnUser() {

        List<Permission> permissions = seeder.permissions(PermissionOption.READ_POSTS, PermissionOption.CREATE_POSTS);
        Role role = seeder.role("User", permissions);

        User user = new User();
        user.setPassword("password");
        user.setRole(role);
        user.setUsername("username");
        user.setEmail("useremail@abv.bg");
        userRepository.save(user);

        User foundUser = userService.findByUsername("username");

        assertNotNull(foundUser);
        assertEquals("username", foundUser.getUsername());
        assertEquals("useremail@abv.bg", foundUser.getEmail());
    }

    @Test
    void findByUsername_UserNotFound_ShouldThrowException() {

        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("nonExistentUser"));
    }


    @Test
    void testAuthenticateWithoutLoginPayload() {
        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(new LoginPayload(), "dummy-agent"));
    }

    @Test
    void testAuthenticateWithLoginPayloadHavingOnlyUsername() {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("dummy");

        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "dummy-agent"));
    }

    @Test
    void testAuthenticateWithLoginPayloadHavingOnlyPassword() {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("password");

        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "dummy-agent"));
    }

    @Test
    void testAuthenticateWithNotExistingUsernameAndPassword() {
        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("dummy");
        loginPayload.setPassword("dummy123");

        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "dummy-agent"));
    }

    @Test
    void testAuthenticateWithUsernameAndPasswordWhileNoRefreshToken() {
        Role role = seeder.role("Admin", seeder.permissions());
        seeder.user("dummy", "dummy@gmail.com", "password", role);

        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("dummy");
        loginPayload.setPassword("password");

        AuthResponse authResponse = userService.authenticate(loginPayload, "dummy-agent");
        Assertions.assertNotNull(authResponse.getToken());
        Assertions.assertNotNull(authResponse.getRefreshToken());

        // Assure valid
        Assertions.assertFalse(jwtService.isTokenExpired(authResponse.getToken()));
        Assertions.assertFalse(jwtService.isTokenExpired(authResponse.getRefreshToken()));

        Claims claims = jwtService.decodeToken(authResponse.getToken());
        Assertions.assertEquals(JwtService.ISSUER, claims.getIssuer());
        Assertions.assertEquals(role.getName(), claims.get(JwtService.ROLE));
        Assertions.assertNotNull(claims.get(JwtService.ID));
        Assertions.assertEquals(loginPayload.getUsername(), claims.get(JwtService.USERNAME));
        Assertions.assertEquals("dummy@gmail.com", claims.get(JwtService.EMAIL));
        Assertions.assertTrue(Arrays.stream(PermissionOption.values()).map(PermissionOption::getAbbreviation).toList().containsAll((List<String>) claims.get(JwtService.PERMISSIONS)));
        Assertions.assertNotNull(claims.getIssuedAt());
        Assertions.assertNotNull(claims.getExpiration());
        Assertions.assertNotNull(claims.getExpiration());

        Assertions.assertTrue(refreshTokenRepository.existsByToken(authResponse.getRefreshToken()));
    }

    @Test
    @Transactional
    void testAuthenticateWithUsernameAndPasswordWithExistingRefreshToken() {
        Role role = seeder.role("Admin", seeder.permissions());
        User user = seeder.user("dummy", "dummy@gmail.com", "password", role);
        Instant initialExpiration = Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY);
        RefreshToken refreshToken = seeder.refreshToken("dummy", Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY), user,null);
        String initialToken = refreshToken.getToken();

        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("dummy");
        loginPayload.setPassword("password");

        AuthResponse authResponse = userService.authenticate(loginPayload, "user-agent");
        Assertions.assertNotNull(authResponse.getToken());
        Assertions.assertNotNull(authResponse.getRefreshToken());

        // Assure valid
        Assertions.assertFalse(jwtService.isTokenExpired(authResponse.getToken()));
        Assertions.assertFalse(jwtService.isTokenExpired(authResponse.getRefreshToken()));

        Claims claims = jwtService.decodeToken(authResponse.getToken());
        Assertions.assertEquals(JwtService.ISSUER, claims.getIssuer());
        Assertions.assertEquals(role.getName(), claims.get(JwtService.ROLE));
        Assertions.assertNotNull(claims.get(JwtService.ID));
        Assertions.assertEquals(loginPayload.getUsername(), claims.get(JwtService.USERNAME));
        Assertions.assertEquals("dummy@gmail.com", claims.get(JwtService.EMAIL));
        Assertions.assertTrue(Arrays.stream(PermissionOption.values()).map(PermissionOption::getAbbreviation).toList().containsAll((List<String>) claims.get(JwtService.PERMISSIONS)));
        Assertions.assertNotNull(claims.getIssuedAt());
        Assertions.assertNotNull(claims.getExpiration());
        Assertions.assertNotNull(claims.getExpiration());

        Optional<RefreshToken> renewedRefreshTokenOpt = refreshTokenRepository.findByToken(authResponse.getRefreshToken());
        Assertions.assertTrue(renewedRefreshTokenOpt.isPresent());

        Assertions.assertTrue(renewedRefreshTokenOpt.get().getExpiresAt().isAfter(initialExpiration));
        Assertions.assertNotEquals(renewedRefreshTokenOpt.get().getToken(), initialToken);
    }

    @Test
    void testAuthenticateWithNotSameUsernameAndPassword() {
        Role role = seeder.role("Admin", seeder.permissions());
        seeder.user("dummy", "dummy@gmail.com", "password", role);

        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setUsername("dummy");
        loginPayload.setPassword("passworde");

        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "dummy-agent"));
    }

    @Test
    @Transactional
    void testAuthenticateWithRefreshToken() {
        Role role = seeder.role("Admin", seeder.permissions());
        User user = seeder.user("dummy", "dummy@gmail.com", "password", role);
        Instant initialExpiration = Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY);
        RefreshToken refreshToken = seeder.refreshToken("dummy", Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY), user,null);
        String initialToken = refreshToken.getToken();

        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setRefreshToken(refreshToken.getToken());

        AuthResponse authResponse = userService.authenticate(loginPayload, "user-agent");

        Assertions.assertFalse(jwtService.isTokenExpired(authResponse.getToken()));
        Assertions.assertFalse(jwtService.isTokenExpired(authResponse.getRefreshToken()));

        Claims claims = jwtService.decodeToken(authResponse.getToken());
        Assertions.assertEquals(JwtService.ISSUER, claims.getIssuer());
        Assertions.assertEquals(role.getName(), claims.get(JwtService.ROLE));
        Assertions.assertNotNull(claims.get(JwtService.ID));
        Assertions.assertEquals(user.getUsername(), claims.get(JwtService.USERNAME));
        Assertions.assertEquals(user.getEmail(), claims.get(JwtService.EMAIL));
        Assertions.assertTrue(Arrays.stream(PermissionOption.values()).map(PermissionOption::getAbbreviation).toList().containsAll((List<String>) claims.get(JwtService.PERMISSIONS)));
        Assertions.assertNotNull(claims.getIssuedAt());
        Assertions.assertNotNull(claims.getExpiration());
        Assertions.assertNotNull(claims.getExpiration());

        Optional<RefreshToken> renewedRefreshTokenOpt = refreshTokenRepository.findByToken(authResponse.getRefreshToken());
        Assertions.assertTrue(renewedRefreshTokenOpt.isPresent());

        Assertions.assertTrue(renewedRefreshTokenOpt.get().getExpiresAt().isAfter(initialExpiration));
        Assertions.assertNotEquals(renewedRefreshTokenOpt.get().getToken(), initialToken);
    }

    @Test
    @Transactional
    void testAuthenticateWithRefreshTokenAndInvalidDifferentUserAgent() {
        Role role = seeder.role("Admin", seeder.permissions());
        User user = seeder.user("dummy", "dummy@gmail.com", "password", role);
        RefreshToken refreshToken = seeder.refreshToken("dummy", Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY), user,null);

        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setRefreshToken(refreshToken.getToken());

        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "user-dummy-agent"));
    }

    @Test
    @Transactional
    void testAuthenticateWithOldRefreshToken() {
        Role role = seeder.role("Admin", seeder.permissions());
        User user = seeder.user("dummy", "dummy@gmail.com", "password", role);
        RefreshToken refreshToken = seeder.refreshToken("dummy", Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY), user,"dummy-refresh-token-old");

        LoginPayload loginPayload = new LoginPayload();
        loginPayload.setRefreshToken(refreshToken.getOldToken());

        Assertions.assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(loginPayload, "user-agent"));
    }
}
