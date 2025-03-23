package com.example.Blog.Project.unit;

import com.example.Blog.Project.file.model.File;
import com.example.Blog.Project.file.repository.FileRepository;
import com.example.Blog.Project.file.service.FileService;
import com.example.Blog.Project.post.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RestTemplate restTemplate;

    @Test
    public void testFindByUuid_Success() {

        UUID uuid = UUID.randomUUID();
        File file = new File();
        file.setContent(new byte[]{1, 2, 3});

        when(fileRepository.findById(uuid)).thenReturn(Optional.of(file));

        byte[] result = fileService.findByUuid(uuid);

        assertNotNull(result);
        assertArrayEquals(new byte[]{1, 2, 3}, result);
        verify(fileRepository).findById(uuid);
    }

    @Test
    public void testFindByUuid_FileNotFound_ShouldThrowException() {

        UUID uuid = UUID.randomUUID();

        when(fileRepository.findById(uuid)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> fileService.findByUuid(uuid));
        assertEquals("File does not exist", exception.getMessage());

        verify(fileRepository).findById(uuid);
    }

    @Test
    public void testCreateFile_Success() throws IOException {

        MultipartFile input = mock(MultipartFile.class);
        byte[] fileContent = new byte[]{1, 2, 3};
        File savedFile = new File();
        savedFile.setContent(fileContent);

        when(input.getSize()).thenReturn(3L);
        when(input.getBytes()).thenReturn(fileContent);
        when(fileRepository.save(any(File.class))).thenReturn(savedFile);

        File result = fileService.create(input);

        assertNotNull(result);
        assertArrayEquals(fileContent, result.getContent());
        verify(fileRepository).save(any(File.class));
    }

    @Test
    public void testCreateFile_InvalidFile_ShouldThrowException() throws IOException {
        MultipartFile input = mock(MultipartFile.class);

        when(input.getSize()).thenReturn(0L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileService.create(input));
        assertEquals("Invalid file", exception.getMessage());

        verify(fileRepository, never()).save(any(File.class));
    }

    @Test
    public void testIsUuid_ValidUuid() {
        String validUuid = UUID.randomUUID().toString();

        boolean result = fileService.isUuid(validUuid);

        assertTrue(result);
    }

    @Test
    public void testIsUuid_InvalidUuid() {

        String invalidUuid = "invalid-uuid";

        boolean result = fileService.isUuid(invalidUuid);

        assertFalse(result);
    }

    @Test
    public void testDeleteUnusedFiles() {

        List<File> files = List.of(new File(), new File());
        when(fileRepository.findAll()).thenReturn(files);
        fileService.deleteUnusedFiles();

    }

    @Test
    public void testDeleteUnusedFiles_NoFilesToDelete() {

        List<File> files = List.of(new File());
        when(fileRepository.findAll()).thenReturn(files);

        fileService.deleteUnusedFiles();
    }
}
