package com.example.Blog.Project.api;

import com.example.Blog.Project.interceptors.RequestInterceptor;
import com.example.Blog.Project.security.JwtService;
import com.example.Blog.Project.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Import(SecurityConfig.class)
public class BaseApiTest {

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected RequestInterceptor requestInterceptor;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() throws Exception {
        when(requestInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }
}
