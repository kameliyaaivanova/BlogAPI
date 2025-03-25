package com.example.Blog.Project.integration;

import com.example.Blog.Project.activity.DeletedFiles;
import com.example.Blog.Project.file.model.File;
import com.example.Blog.Project.file.repository.FileRepository;
import com.example.Blog.Project.file.service.FileService;
import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class FileServiceTest extends BaseTest {

    @MockitoBean
    private RestTemplate restTemplate;

    @MockitoBean
    private FileRepository fileRepository;

    @MockitoBean
    private PostRepository postRepository;

    @Autowired
    private FileService fileService;

    @Value("${statistics.service.baseUrl}")
    private String statisticsServiceUrl;

    @BeforeEach
    public void setUp() {
        fileRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    public void testCreateFile_ShouldThrowException_WhenFileIsEmpty() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(0L);

        assertThrows(IllegalArgumentException.class, () -> fileService.create(mockFile));
    }

//    @Test
//    public void testFindByUuid_ShouldReturnFileContent_WhenFileExists() {
//        File file = new File();
//        byte[] content = "file content".getBytes();
//        file.setContent(content);
//        file.setId(UUID.randomUUID());
//        file = fileRepository.save(file);
//
//        byte[] foundContent = fileService.findByUuid(file.getId());
//
//        assertNotNull(foundContent);
//        assertArrayEquals(content, foundContent);
//    }

    @Test
    public void testFindByUuid_ShouldThrowException_WhenFileDoesNotExist() {
        UUID nonExistentUuid = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class, () -> fileService.findByUuid(nonExistentUuid));
    }

    @Test
    public void testIsUuid_ShouldReturnTrue_WhenValidUuid() {
        String validUuid = UUID.randomUUID().toString();
        boolean result = fileService.isUuid(validUuid);

        assertTrue(result);
    }

    @Test
    public void testIsUuid_ShouldReturnFalse_WhenInvalidUuid() {
        String invalidUuid = "invalid-uuid";
        boolean result = fileService.isUuid(invalidUuid);

        assertFalse(result);
    }

    @Test
    public void testDeleteUnusedFiles_ShouldSendToStatisticsService() throws Exception {
        File file = new File();
        file.setContent("some content".getBytes());
        fileRepository.save(file);

        fileService.deleteUnusedFiles();
        verify(restTemplate, times(1)).postForObject(eq(statisticsServiceUrl + "/files/add"), any(), eq(DeletedFiles.class));
    }
}