package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.WorkExperienceDto;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import com.buxz.dev.combuxzjobxservice.service.UploadService;
import com.buxz.dev.combuxzjobxservice.service.UserProfileService;
import com.buxz.dev.combuxzjobxservice.service.WorkExperienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/jobx/profile")
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;
    private final UserProfileService userProfileService;
    private final UploadService uploadService;

    @Autowired
    public WorkExperienceController(final WorkExperienceService workExperienceService,
                                    final UserProfileService userProfileService,
                                    final UploadService uploadService) {
        this.workExperienceService = workExperienceService;
        this.userProfileService = userProfileService;
        this.uploadService = uploadService;
    }

    @PostMapping("/{userid}/work")
    private ResponseEntity<WorkExperienceEntity> addWorkExperience(@PathVariable("userid") int id, @RequestBody WorkExperienceDto workExperienceDto) {
        try {
            Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(id);
            if (userProfile.isPresent()) {
                return new ResponseEntity<>(workExperienceService.addWorkExperience(id, workExperienceDto), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("Failed to AddWorkExperience to userProfile {}, due to {}", id, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userid}/work/{id}/update")
    private ResponseEntity<WorkExperienceEntity> updateWorkExperience(@PathVariable("userid")
            int userId, @PathVariable("id") int workId, @RequestBody WorkExperienceDto workExperienceDto) {
        try {
            Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(userId);
            Optional<WorkExperienceEntity> workExperience = workExperienceService.getWorkExperienceById(workId);
            if (userProfile.isPresent() && workExperience.isPresent()) {
                return new ResponseEntity<>(workExperienceService.updateWorkExperience(userId, workId, workExperienceDto), HttpStatus.OK);
            } else {
                log.warn("Failed to UpdateWorkExperience because UserProfile {} or WorkExperience {} not Found", userId, workId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Failed to UpdateWorkExperience for UserProfile {} due to : {}", userId, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userid}/work/{id}/delete")
    private ResponseEntity<ResponseMessage> deleteWorkExperience(@PathVariable("userid")
            int userId, @PathVariable("id") int workId) {
        try {
            Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(userId);
            Optional<WorkExperienceEntity> workExperience = workExperienceService.getWorkExperienceById(workId);
            if (userProfile.isPresent() && workExperience.isPresent()) {
                return new ResponseEntity<>(workExperienceService.deleteWorkExperience(userId, workId), HttpStatus.GONE);
            } else {
                log.warn("Failed to DeleteWorkExperience because UserProfile {} or WorkExperience {} not Found", userId, workId);
                return new ResponseEntity<>(new ResponseMessage("Failed to DeleteWorkExperience : UserProfile or WorkExperience not Found"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.warn("DeleteWorkExperience : WorkExperience {} for UserProfile {} Failed to delete due to : {}", userId, workId, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{userid}/work/upload")
    public ResponseEntity<ResponseMessage> uploadSupportingImages(@RequestParam("file") MultipartFile file) throws IOException {
        String message = "";
        if (uploadService.checkIfImageFile(file)) {
            try {
                uploadService.uploadFile(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                log.info("Could not upload the file: Due to {}", e.getLocalizedMessage());
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        } else {
            message = "Uploaded incorrect image type: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
        }
    }
}
