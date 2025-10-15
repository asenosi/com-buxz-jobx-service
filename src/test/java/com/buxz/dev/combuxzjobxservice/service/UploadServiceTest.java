package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.util.AmazonS3Util;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadServiceTest {

    @Mock
    private AmazonS3Util amazonS3Util;

    @InjectMocks
    private UploadService uploadService;

    @AfterEach
    void cleanUpFiles() {
        deleteIfExists("test-upload.pdf");
        deleteIfExists("test-empty.pdf");
        deleteIfExists("image-valid.png");
        deleteIfExists("text-file.txt");
    }

    private void deleteIfExists(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void isEmptyFile_ShouldReturnTrueForNonEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test-upload.pdf", UploadService.PDF_TYPE, "content".getBytes());

        assertTrue(uploadService.isEmptyFile(file));
    }

    @Test
    void isEmptyFile_ShouldReturnFalseForEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test-empty.pdf", UploadService.PDF_TYPE, new byte[0]);

        assertFalse(uploadService.isEmptyFile(file));
    }

    @Test
    void checkIfDocumentFile_ShouldReturnTrueForValidTypes() {
        MockMultipartFile file = new MockMultipartFile("file", "test-upload.pdf", UploadService.PDF_TYPE, "content".getBytes());

        assertTrue(uploadService.checkIfDocumentFile(file));
    }

    @Test
    void checkIfDocumentFile_ShouldReturnFalseForInvalidType() {
        MockMultipartFile file = new MockMultipartFile("file", "test-upload.pdf", "text/plain", "content".getBytes());

        assertFalse(uploadService.checkIfDocumentFile(file));
    }

    @Test
    void uploadFile_ShouldDelegateToAmazonS3Util() {
        MockMultipartFile file = new MockMultipartFile("file", "test-upload.pdf", UploadService.PDF_TYPE, "content".getBytes());
        doNothing().when(amazonS3Util).uploadToS3Bucket(any(), any());
        ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);

        uploadService.uploadFile(file);

        verify(amazonS3Util).uploadToS3Bucket(any(), fileCaptor.capture());
        assertTrue(fileCaptor.getValue().exists());
        fileCaptor.getValue().delete();
    }

    @Test
    void uploadFile_WhenAmazonS3Throws_ShouldWrapException() {
        MockMultipartFile file = new MockMultipartFile("file", "test-upload.pdf", UploadService.PDF_TYPE, "content".getBytes());
        doThrow(new RuntimeException("failure")).when(amazonS3Util).uploadToS3Bucket(any(), any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> uploadService.uploadFile(file));
        assertTrue(exception.getMessage().contains("Failed to upload file"));
    }

    @Test
    void checkIfImageFile_ShouldReturnTrueForImage() throws IOException {
        byte[] pngHeader = new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 26, 10};
        MockMultipartFile file = new MockMultipartFile("file", "image-valid.png", "image/png", pngHeader);

        assertTrue(uploadService.checkIfImageFile(file));
    }

    @Test
    void checkIfImageFile_ShouldReturnFalseForNonImage() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "text-file.txt", "text/plain", "text".getBytes());

        assertFalse(uploadService.checkIfImageFile(file));
    }
}
