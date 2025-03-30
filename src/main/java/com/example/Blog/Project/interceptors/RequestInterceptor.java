package com.example.Blog.Project.interceptors;

import com.example.Blog.Project.activity.Activity;
import com.example.Blog.Project.activity.ActivityPayload;
import com.example.Blog.Project.security.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;

    private final String statisticsServiceUrl;

    private final SecurityService securityService;

    @Autowired
    public RequestInterceptor(RestTemplate restTemplate, @Value("${statistics.service.baseUrl}") String statisticsServiceUrl, SecurityService securityService) {
        this.restTemplate = restTemplate;
        this.statisticsServiceUrl = statisticsServiceUrl;
        this.securityService = securityService;
    }

    private final Executor executor = Executors.newFixedThreadPool(15);

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long userId = null;
        try {
            userId = securityService.getUser().getId();
        } catch (Exception ignored) {}

        ActivityPayload activityPayload = new ActivityPayload(request.getRequestURI(), userId);
        executor.execute(() -> {
            HttpEntity<ActivityPayload> payloadHttpEntity = new HttpEntity<>(activityPayload);
            restTemplate.postForObject(statisticsServiceUrl + "/activity/add", payloadHttpEntity, Activity.class);
        });
    }
}