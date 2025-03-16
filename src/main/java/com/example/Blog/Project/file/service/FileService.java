package com.example.Blog.Project.file.service;

import java.util.List;

import com.example.Blog.Project.activity.DeleteFilesPayload;
import com.example.Blog.Project.activity.DeletedFiles;
import com.example.Blog.Project.file.model.File;
import com.example.Blog.Project.file.repository.FileRepository;
import com.example.Blog.Project.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${statistics.service.baseUrl}")
    private String statisticsServiceUrl;

    public byte[] findByUuid(UUID uuid) {
        return fileRepository.findById(uuid).orElseThrow(() -> new EntityNotFoundException("File does not exist")).getContent();
    }

    public File create(MultipartFile input) {
        if (input.getSize() == 0) {
            throw new IllegalArgumentException("Invalid file");
        }

        File file = new File();
        try {
            file.setContent(input.getBytes());
        } catch (IOException e) {
            return null;
        }

        return fileRepository.save(file);
    }

    public boolean isUuid(String value) {
        try {
            UUID.fromString(value);

            return Boolean.TRUE;
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
    }

    @Scheduled(cron = "0 0 4 * * *") // every day at 4 am
    @Transactional
    public void deleteUnusedFiles() {
        List<File> files = fileRepository.findAll();
        long amount = 0;

        for (File file : files) {
            if (!postRepository.existsByLogoContaining(file.getId())) {
                fileRepository.deleteById(file.getId());
                amount++;
            }
        }

        restTemplate.postForObject(statisticsServiceUrl + "/files/add", new DeleteFilesPayload(amount), DeletedFiles.class);
    }
}
