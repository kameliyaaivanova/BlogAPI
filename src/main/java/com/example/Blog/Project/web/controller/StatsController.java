package com.example.Blog.Project.web.controller;

import com.example.Blog.Project.activity.Activity;
import com.example.Blog.Project.activity.DeletedFiles;
import com.example.Blog.Project.activity.service.ActivityService;
import com.example.Blog.Project.page.RestResponsePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private final ActivityService activityService;

    @Autowired
    public StatsController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activity")
    public ResponseEntity<RestResponsePage<Activity>> getActivities(Pageable pageable) {
        return ResponseEntity.ok(activityService.getActivity(pageable));
    }

    @GetMapping("/deleted-files")
    public ResponseEntity<RestResponsePage<DeletedFiles>> getDeletedFiles(Pageable pageable) {
        return ResponseEntity.ok(activityService.getDeletedFiles(pageable));
    }
}
