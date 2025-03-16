package com.example.Blog.Project.activity.service;

import com.example.Blog.Project.activity.Activity;
import com.example.Blog.Project.activity.DeletedFiles;
import com.example.Blog.Project.page.RestResponsePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ActivityService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${statistics.service.baseUrl}")
    private String statisticsServiceUrl;

    public RestResponsePage<Activity> getActivity(Pageable pageable) {
        String url = UriComponentsBuilder.fromHttpUrl(statisticsServiceUrl + "/activity")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .toUriString();

        ParameterizedTypeReference<RestResponsePage<Activity>> responseType = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
        ).getBody();
    }

    public RestResponsePage<DeletedFiles> getDeletedFiles(Pageable pageable) {
        String url = UriComponentsBuilder.fromHttpUrl(statisticsServiceUrl + "/files")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize())
                .toUriString();

        ParameterizedTypeReference<RestResponsePage<DeletedFiles>> responseType = new ParameterizedTypeReference<>() {
        };

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
        ).getBody();
    }
}
