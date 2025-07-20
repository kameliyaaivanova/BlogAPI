package com.example.Blog.Project.security;

import com.example.Blog.Project.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class SecurityService {

    @Autowired
    private ObjectMapper objectMapper;

    public User getUser() {
        Authentication authentication = getAuthentication();

        if (Objects.isNull(authentication) || Objects.isNull(authentication.getPrincipal())) {
            throw new EntityNotFoundException("No authenticated user");
        }

        User user = objectMapper.convertValue(authentication.getPrincipal(), User.class);

        return user;
    }

    public String getToken() {
        Authentication authentication = getAuthentication();

        if (Objects.isNull(authentication) || Objects.isNull(authentication.getCredentials()) || !StringUtils.hasText(authentication.getCredentials().toString())) {
            return null;
        }

        return authentication.getCredentials().toString();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
