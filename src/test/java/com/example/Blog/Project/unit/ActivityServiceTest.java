package com.example.Blog.Project.unit;

import com.example.Blog.Project.activity.Activity;
import com.example.Blog.Project.activity.DeletedFiles;
import com.example.Blog.Project.activity.service.ActivityService;
import com.example.Blog.Project.page.RestResponsePage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceTest {

    @InjectMocks
    private ActivityService activityService;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testGetActivity_Success() {
        Pageable pageable = mock(Pageable.class);
        Activity activity = new Activity();
        activity.setPath("home");
        activity.setUserId(1L);
        Page<Activity> page = new RestResponsePage<>(List.of(activity));

        when(restTemplate.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(null),
                Mockito.any(ParameterizedTypeReference.class))).thenReturn(ResponseEntity.ok(page));

        Page<Activity> result = activityService.getActivity(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        verify(restTemplate).exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(null),
                Mockito.any(ParameterizedTypeReference.class));

    }

    @Test
    public void testGetDeletedFiles_Success() {
        Pageable pageable = mock(Pageable.class);
        when(pageable.getPageNumber()).thenReturn(0);
        when(pageable.getPageSize()).thenReturn(10);

        DeletedFiles deletedFile = new DeletedFiles();
        deletedFile.setAmount(1L);

        Page<DeletedFiles> page = new RestResponsePage<>(List.of(deletedFile));

        when(restTemplate.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(null),
                Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(page));

        RestResponsePage<DeletedFiles> result = activityService.getDeletedFiles(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        verify(restTemplate).exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.eq(null),
                Mockito.any(ParameterizedTypeReference.class));

    }
}
