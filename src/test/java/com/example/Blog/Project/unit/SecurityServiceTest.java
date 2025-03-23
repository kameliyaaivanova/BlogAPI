package com.example.Blog.Project.unit;

import com.example.Blog.Project.security.SecurityService;
import com.example.Blog.Project.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SecurityService securityService;

    @Before
    public void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testGetUser_ShouldReturnUser_WhenAuthenticated() {

        User mockUser = new User();
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(objectMapper.convertValue(mockUser, User.class)).thenReturn(mockUser);

        User result = securityService.getUser();

        assertThat(result).isNotNull();
        verify(objectMapper).convertValue(mockUser, User.class);
    }

    @Test
    public void testGetUser_ShouldThrowException_WhenNoAuthentication() {

        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(EntityNotFoundException.class, securityService::getUser);
    }

    @Test
    public void testGetUser_ShouldThrowException_WhenPrincipalIsNull() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(EntityNotFoundException.class, securityService::getUser);
    }

    @Test
    public void testGetToken_ShouldReturnToken_WhenCredentialsAreValid() {

        String expectedToken = "valid_token";
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(expectedToken);

        String token = securityService.getToken();

        assertThat(token).isEqualTo(expectedToken);
    }

    @Test
    public void testGetToken_ShouldReturnNull_WhenNoAuthentication() {

        when(securityContext.getAuthentication()).thenReturn(null);

        String token = securityService.getToken();

        assertThat(token).isNull();
    }

    @Test
    public void testGetToken_ShouldReturnNull_WhenCredentialsAreNull() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn(null);

        String token = securityService.getToken();

        assertThat(token).isNull();
    }


    @Test
    public void testGetToken_ShouldReturnNull_WhenCredentialsAreEmpty() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getCredentials()).thenReturn("");

        String token = securityService.getToken();

        assertThat(token).isNull();
    }
}
