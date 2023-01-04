package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.util.AmazonS3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Service
public class UploadService {

    public static String PDF_TYPE = "application/pdf";
    public static String DOC_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private final AmazonS3Util amazonS3Util;

    @Autowired
    public UploadService(AmazonS3Util amazonS3Util) {
        this.amazonS3Util = amazonS3Util;
    }

    public boolean isEmptyFile(MultipartFile file) {
        if (!file.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    public boolean checkIfDocumentFile(MultipartFile file) {
        if (PDF_TYPE.equals(file.getContentType()) || DOC_TYPE.equals(file.getContentType())) {
            return true;
        }
        log.info("Received a incorrect file format, Receive file of type {}, which is not PDF", file.getContentType());
        return false;
    }

    public void uploadFile(MultipartFile file) {
        try {
            amazonS3Util.uploadToS3Bucket(file.getOriginalFilename(), convertMultiPartToFile(file));
        } catch (Exception exception) {
            throw new RuntimeException("Failed to upload file due to " + exception.getLocalizedMessage());
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    public boolean checkIfImageFile(MultipartFile file) throws IOException {
        File fileUploaded = convertMultiPartToFile(file);
        String mimeType = Files.probeContentType(fileUploaded.toPath());
        if (mimeType != null && mimeType.split("/")[0].equals("image")) {
            log.debug("Received correct image type");
            return true;
        } else {
            log.debug("Did not receive correct image type");
            return false;
        }
    }
}

