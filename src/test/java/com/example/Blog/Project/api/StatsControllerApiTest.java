package com.example.Blog.Project.api;

import com.example.Blog.Project.activity.Activity;
import com.example.Blog.Project.activity.DeletedFiles;
import com.example.Blog.Project.activity.service.ActivityService;
import com.example.Blog.Project.page.RestResponsePage;
import com.example.Blog.Project.web.controller.StatsController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
public class StatsControllerApiTest extends BaseApiTest {

    @MockitoBean
    private ActivityService activityService;

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.r", "s.r"})
    public void testGetActivities_ShouldReturnActivities_WhenActivitiesExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Activity activity1 = new Activity();
        activity1.setPath("ActivityPath");

        Activity activity2 = new Activity();
        activity2.setPath("ActivityPath2");

        RestResponsePage<Activity> activitiesPage = new RestResponsePage<>(List.of(activity1, activity2), pageable, 2);

        when(activityService.getActivity(Mockito.any())).thenReturn(activitiesPage);

        mockMvc.perform(get("/activity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(activityService, times(1)).getActivity(Mockito.any());
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.r", "s.r"})
    public void testGetActivities_ShouldReturnEmptyList_WhenNoActivitiesExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        RestResponsePage<Activity> emptyActivitiesPage = new RestResponsePage<>(Collections.emptyList(), pageable, 0);

        when(activityService.getActivity(Mockito.any())).thenReturn(emptyActivitiesPage);

        mockMvc.perform(get("/activity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));

        verify(activityService, times(1)).getActivity(Mockito.any());
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.r", "s.r"})
    public void testGetDeletedFiles_ShouldReturnDeletedFiles_WhenDeletedFilesExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        DeletedFiles deletedFile1 = new DeletedFiles();
        deletedFile1.setAmount(1L);

        DeletedFiles deletedFile2 = new DeletedFiles();
        deletedFile2.setAmount(2L);

        RestResponsePage<DeletedFiles> deletedFilesPage = new RestResponsePage<>(List.of(deletedFile1, deletedFile2), pageable, 2);

        when(activityService.getDeletedFiles(Mockito.any())).thenReturn(deletedFilesPage);

        mockMvc.perform(get("/deleted-files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));

        verify(activityService, times(1)).getDeletedFiles(Mockito.any());
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"p.r", "s.r"})
    public void testGetDeletedFiles_ShouldReturnEmptyList_WhenNoDeletedFilesExist() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        RestResponsePage<DeletedFiles> emptyDeletedFilesPage = new RestResponsePage<>(Collections.emptyList(), pageable, 0);

        when(activityService.getDeletedFiles(Mockito.any())).thenReturn(emptyDeletedFilesPage);

        mockMvc.perform(get("/deleted-files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));

        verify(activityService, times(1)).getDeletedFiles(Mockito.any());
    }

    @Test
    @WithMockUser(username = "User", authorities = {"u.r"})
    public void testGetActivities_ShouldReturnForbidden_WhenUserHasNoPermission() throws Exception {

        mockMvc.perform(get("/activity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(activityService, times(0)).getActivity(Mockito.any());
    }

    @Test
    @WithMockUser(username = "User", authorities = {"u.r"})
    public void testGetDeletedFiles_ShouldReturnForbidden_WhenUserHasNoPermission() throws Exception {

        mockMvc.perform(get("/deleted-files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(activityService, times(0)).getDeletedFiles(Mockito.any());
    }
}
