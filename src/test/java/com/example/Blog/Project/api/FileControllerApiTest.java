package com.example.Blog.Project.api;

import com.example.Blog.Project.file.model.File;
import com.example.Blog.Project.file.service.FileService;
import com.example.Blog.Project.web.controller.FileController;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;


@WebMvcTest(FileController.class)
public class FileControllerApiTest extends BaseApiTest {

    @MockitoBean
    private FileService fileService;

    @Test
    void testFindByUuid_ShouldReturnFile_WhenValidUuidIsProvided() throws Exception {
        UUID validUuid = UUID.randomUUID();
        byte[] fileData = "Sample File Data".getBytes();

        when(fileService.findByUuid(validUuid)).thenReturn(fileData);

        mockMvc.perform(get("/files/{uuid}", validUuid)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(fileData));
    }

    @Test
    void testFindByUuid_ShouldReturnNotFound_WhenFileDoesNotExist() throws Exception {
        UUID nonExistentUuid = UUID.randomUUID();

        when(fileService.findByUuid(nonExistentUuid)).thenThrow(new EntityNotFoundException("File not found"));

        mockMvc.perform(get("/files/{uuid}", nonExistentUuid)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindByUuid_ShouldReturnBadRequest_WhenUuidIsInvalid() throws Exception {
        String invalidUuid = "invalid-uuid";

        mockMvc.perform(get("/files/{uuid}", invalidUuid)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.c","p.u"})
    void testCreateFile_ShouldReturnOk_WhenFileIsUploaded() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile("file", "testFile.txt", "text/plain", "content".getBytes());

        File createdFile = new File();

        UUID fileUUID = UUID.randomUUID();
        createdFile.setId(fileUUID);
        when(fileService.create(any(MultipartFile.class))).thenReturn(createdFile);

        mockMvc.perform(multipart("/files/add")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.c","p.u"})
    void testCreateFile_ShouldReturnUnprocessableEntity_WhenFileUploadFails() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile("file", "testFile.txt", "text/plain", "content".getBytes());

        when(fileService.create(any(MultipartFile.class))).thenReturn(null);

        mockMvc.perform(multipart("/files/add")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.c"})
    void testCreateFile_ShouldReturnBadRequest_WhenNoFileProvided() throws Exception {

        mockMvc.perform(multipart("/files/add")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.c"})
    void testCreateFile_ShouldReturnBadRequest_WhenFileIsEmpty() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile("file", "emptyFile.txt", "text/plain", new byte[0]);

        when(fileService.create(any(MultipartFile.class))).thenThrow(new IllegalArgumentException("Invalid file"));

        mockMvc.perform(multipart("/files/add")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());
    }

}
