package com.example.Blog.Project.web.controller;

import com.example.Blog.Project.file.model.File;
import com.example.Blog.Project.file.model.FileResponse;
import com.example.Blog.Project.file.service.FileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> findByUuid(@Valid @NotBlank @PathVariable("uuid") UUID uuid) {
        return ResponseEntity.ok(fileService.findByUuid(uuid));
    }

    @PostMapping("/add")
    public ResponseEntity<FileResponse> create(@RequestPart(value = "file") MultipartFile file){
        File createdFile = fileService.create(file);

        if (createdFile == null) {
            return ResponseEntity.unprocessableEntity().build();
        }

        return ResponseEntity.ok(new FileResponse(createdFile.getId()));
    }
}
