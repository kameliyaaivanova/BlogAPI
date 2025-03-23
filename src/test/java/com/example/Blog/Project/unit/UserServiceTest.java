package com.example.Blog.Project.unit;

import com.example.Blog.Project.exception.AuthenticationFailedException;
import com.example.Blog.Project.refreshtoken.model.RefreshToken;
import com.example.Blog.Project.refreshtoken.repository.RefreshTokenRepository;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.security.JwtService;
import com.example.Blog.Project.user.model.AuthResponse;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.repository.UserRepository;
import com.example.Blog.Project.user.service.UserService;
import com.example.Blog.Project.web.dto.AddUserPayload;
import com.example.Blog.Project.web.dto.LoginPayload;
import com.example.Blog.Project.web.dto.RegisterPayload;
import com.example.Blog.Project.web.dto.UpdateUserPayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    public void testLoadUserByUsername_Success() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        Throwable result = assertThrows(EntityNotFoundException.class, () -> userService.loadUserByUsername(username));
        assertEquals("Could not find user", result.getMessage());
    }

    @Test
    public void testRegister_Success() {
        RegisterPayload payload = new RegisterPayload();
        payload.setUsername("newUser");
        payload.setEmail("newUser@example.com");
        payload.setPassword("password123");

        Role userRole = new Role();
        userRole.setName("User");

        User mappedUser = new User();
        mappedUser.setUsername(payload.getUsername());
        mappedUser.setEmail(payload.getEmail());

        when(userRepository.existsByUsernameOrEmail(payload.getUsername(), payload.getEmail())).thenReturn(false);
        when(roleRepository.findByName("User")).thenReturn(Optional.of(userRole));
        when(modelMapper.map(payload, User.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode(payload.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = userService.register(payload);

        assertTrue(result);
        assertEquals("encodedPassword", mappedUser.getPassword());
        assertEquals(userRole, mappedUser.getRole());
        assertTrue(mappedUser.isEnabled());
        verify(userRepository).save(mappedUser);
    }

    @Test
    public void testRegister_WhenUsernameOrEmailExists() {
        RegisterPayload payload = new RegisterPayload();
        payload.setUsername("existingUser");
        payload.setEmail("existing@example.com");

        when(userRepository.existsByUsernameOrEmail(payload.getUsername(), payload.getEmail())).thenReturn(true);

        Throwable result = assertThrows(IllegalArgumentException.class, () -> userService.register(payload));

        assertEquals("Username or email already exists.", result.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testGetAll() {

        Pageable pageable = PageRequest.of(0, 10); // Page 0 with 10 elements per page
        Page<User> page = mock(Page.class);  // Mocking the Page<User> result
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAll(pageable);

        assertNotNull(result);
        verify(userRepository).findAll(pageable);
    }

    @Test
    public void testUpdate_Success() {

        long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldUsername");
        existingUser.setEmail("oldEmail@example.com");
        existingUser.setPassword("oldPassword");

        UpdateUserPayload updateUserPayload = new UpdateUserPayload();
        updateUserPayload.setUsername("newUsername");
        updateUserPayload.setEmail("newEmail@example.com");
        updateUserPayload.setPassword("newPassword");

        User savedUser = new User();
        savedUser.setId(userId);
        savedUser.setUsername("newUsername");
        savedUser.setEmail("newEmail@example.com");
        savedUser.setPassword("encodedNewPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updateUserPayload.getPassword())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.update(userId, updateUserPayload);

        assertNotNull(result);
        assertEquals("newUsername", result.getUsername());
        assertEquals("newEmail@example.com", result.getEmail());
        assertEquals("encodedNewPassword", result.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testUpdate_UserNotFound() {
        long userId = 1L;

        UpdateUserPayload updateUserPayload = new UpdateUserPayload();
        updateUserPayload.setUsername("newUsername");
        updateUserPayload.setEmail("newEmail@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Throwable result = assertThrows(EntityNotFoundException.class, () -> userService.update(userId, updateUserPayload));
        assertEquals("User not found", result.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void testCreate_Success() {
        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("newUser");
        addUserPayload.setEmail("newUser@example.com");
        addUserPayload.setPassword("password123");

        User newUser = new User();
        newUser.setUsername(addUserPayload.getUsername());
        newUser.setEmail(addUserPayload.getEmail());
        newUser.setPassword("encodedPassword");

        when(userRepository.existsByUsernameOrEmail(addUserPayload.getUsername(), addUserPayload.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(addUserPayload.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = userService.create(addUserPayload);

        assertNotNull(result);
        assertEquals(addUserPayload.getUsername(), result.getUsername());
        assertEquals(addUserPayload.getEmail(), result.getEmail());
        assertEquals("encodedPassword", result.getPassword());

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testCreate_WhenUsernameOrEmailExists() {
        AddUserPayload addUserPayload = new AddUserPayload();
        addUserPayload.setUsername("existingUser");
        addUserPayload.setEmail("existing@example.com");

        when(userRepository.existsByUsernameOrEmail(addUserPayload.getUsername(), addUserPayload.getEmail())).thenReturn(true);

        Throwable result = assertThrows(IllegalArgumentException.class, () -> userService.create(addUserPayload));
        assertEquals("Username or email already exist!", result.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void testDelete_Success() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDelete_WhenUserNotFound() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        Throwable result = assertThrows(IllegalArgumentException.class, () -> userService.delete(userId));
        assertEquals("User not found with id: " + userId, result.getMessage());

        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testFindById_Success() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testUser", result.getUsername());

        verify(userRepository).findById(userId);
    }

    @Test
    public void testFindById_UserNotFound() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Throwable result = assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
        assertEquals("User with id [1] does not exist.", result.getMessage());

        verify(userRepository).findById(userId);
    }

    @Test
    public void testFindByUsername_Success() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        user.setEmail("test@example.com");

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.findByUsername(username);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findUserByUsername(username);
    }

    @Test
    public void testFindByUsername_UserNotFound() {
        String username = "nonExistentUser";

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        Throwable result = assertThrows(EntityNotFoundException.class, () -> userService.findByUsername(username));
        assertEquals("User with username: nonExistentUser was not found.", result.getMessage());

        verify(userRepository).findUserByUsername(username);
    }

    @Test
    public void testAuthenticate() {
        LoginPayload payload = new LoginPayload();

        Throwable result = assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(payload, null));
        assertEquals("Invalid credentials", result.getMessage());
    }

    @Test
    public void testAuthenticateWithRefreshTokenTokenIsNotFoundInDb() {
        LoginPayload payload = new LoginPayload();
        payload.setRefreshToken("dummy");

        Throwable result = assertThrows(AuthenticationFailedException.class, () -> userService.authenticate(payload, null));
        assertEquals("Invalid credentials", result.getMessage());
    }

    @Test
    public void testAuthenticateWithRefreshTokenWhenUserDoesNotExist() {
        LoginPayload payload = new LoginPayload();
        payload.setRefreshToken("dummy");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("dummy");
        refreshToken.setId(1L);
        Mockito.when(refreshTokenRepository.findByTokenAndUserAgentAndExpiresAtAfter(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(refreshToken));

        Throwable result = assertThrows(EntityNotFoundException.class, () -> userService.authenticate(payload, null));
        assertEquals("User with id [1] does not exist.", result.getMessage());
    }

    @Test
    public void testAuthenticateWithRefreshTokenWhenUserExist() {
        LoginPayload payload = new LoginPayload();
        payload.setRefreshToken("dummy");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("dummy");
        refreshToken.setId(1L);
        Mockito.when(refreshTokenRepository.findByTokenAndUserAgentAndExpiresAtAfter(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(refreshToken));

        User user = new User();
        user.setId(1L);
        user.setUsername("dummy");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(jwtService.generateToken(user)).thenReturn("dummy-token");
        Mockito.when(refreshTokenRepository.save(Mockito.any())).then(a -> a.getArguments()[0]);
        Mockito.when(jwtService.generateRefreshToken()).thenReturn("new-refresh-token");

        AuthResponse authResponse = userService.authenticate(payload, null);
        Assertions.assertEquals("new-refresh-token", authResponse.getRefreshToken());
        Assertions.assertEquals("dummy-token", authResponse.getToken());
    }

    @Test
    public void testRenewRefreshToken_WhenRefreshTokenExists() {

        User user = new User();
        user.setId(1L);
        String userAgent = "Mozilla/5.0";

        RefreshToken existingToken = new RefreshToken();
        existingToken.setOldToken("oldToken");
        existingToken.setUser(user);
        existingToken.setUserAgent(userAgent);
        existingToken.setToken("oldTokenValue");
        existingToken.setExpiresAt(Instant.now().plusSeconds(3600));

        String newRefreshToken = "newRefreshTokenValue";
        when(jwtService.generateRefreshToken()).thenReturn(newRefreshToken);


        String result = ReflectionTestUtils.invokeMethod(userService, "renewRefreshToken", user, existingToken, userAgent);

        assertNotNull(result);
        assertEquals(newRefreshToken, result);
        verify(refreshTokenRepository, times(1)).save(existingToken);
    }
}
