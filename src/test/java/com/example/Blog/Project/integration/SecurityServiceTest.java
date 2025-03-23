package com.example.Blog.Project.integration;

import com.example.Blog.Project.security.SecurityService;
import com.example.Blog.Project.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class SecurityServiceTest {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUser_ShouldReturnAuthenticatedUser() {

        User mockUser = new User();
        mockUser.setUsername("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User result = securityService.getUser();

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUser_ShouldThrowException_WhenNoAuthenticatedUser() {

        SecurityContextHolder.clearContext();

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, securityService::getUser);
        assertEquals("No authenticated user", thrown.getMessage());
    }

    @Test
    void getToken_ShouldReturnToken() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn("testToken");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String token = securityService.getToken();

        assertNotNull(token);
        assertEquals("testToken", token);
    }

    @Test
    void getToken_ShouldReturnNull_WhenNoTokenPresent() {

        Authentication authentication = mock(Authentication.class);
        when(authentication.getCredentials()).thenReturn(null);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        String token = securityService.getToken();

        assertNull(token);
    }
}
