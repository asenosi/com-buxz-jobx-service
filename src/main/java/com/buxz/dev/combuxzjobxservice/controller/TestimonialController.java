package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.TestimonialDto;
import com.buxz.dev.combuxzjobxservice.entity.TestimonialEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.service.TestimonialService;
import com.buxz.dev.combuxzjobxservice.service.UploadService;
import com.buxz.dev.combuxzjobxservice.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/jobx/profile")
public class TestimonialController {

    @Autowired
    private TestimonialService testimonialService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UploadService uploadService;

    @PostMapping("/{userid}/testimonial")
    private ResponseEntity<TestimonialEntity> addTestimonial(@PathVariable("userid") int userid,
            @RequestBody TestimonialDto testimonialDto) {
        try {
            Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(userid);
            if (userProfile.isPresent()) {
                return new ResponseEntity<>(testimonialService.addTestimonial(userid, testimonialDto), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("Failed to AddTestimonial to userProfile {}, due to {}", userid, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userid}/testimonial/{id}/update")
    private ResponseEntity<TestimonialEntity> updateTestimonial(@PathVariable("userid")
            int userId, @PathVariable("id") int testimonialId, @RequestBody TestimonialDto testimonialDto) {
        try {
            Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(userId);
            Optional<TestimonialEntity> testimonial = testimonialService.getTestimonialById(testimonialId);
            if (userProfile.isPresent() && testimonial.isPresent()) {
                return new ResponseEntity<>(testimonialService.updateTestimonial(userId, testimonialId, testimonialDto), HttpStatus.ACCEPTED);
            } else {
                log.warn("Failed to UpdateTestimonial because UserProfile {} or WorkExperience {} Not Found", userId, testimonialId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Failed to UpdateTestimonial for UserProfile {} due to : {}", userId, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userid}/testimonial/{id}/delete")
    private ResponseEntity<ResponseMessage> deleteTestimonial(@PathVariable("userid")
            int userId, @PathVariable("id") int testimonialId) {
        try {
            Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(userId);
            Optional<TestimonialEntity> testimonial = testimonialService.getTestimonialById(testimonialId);
            if (userProfile.isPresent() && testimonial.isPresent()) {
                return new ResponseEntity<>(testimonialService.deleteTestimonial(userId, testimonialId), HttpStatus.GONE);
            } else {
                log.warn("Failed to DeleteTestimonial because UserProfile {} or Testimonial {} not Found", userId, testimonialId);
                return new ResponseEntity<>(new ResponseMessage("Failed to DeleteWorkExperience : UserProfile or Testimonial not Found"), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.warn("DeleteTestimonial : Testimonial {} for UserProfile {} Failed to delete due to : {}", userId, testimonialId, e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{userid}/upload")
    public ResponseEntity<ResponseMessage> uploadTestimonialFile(@PathVariable("userid") int userId, @RequestParam("file") MultipartFile file) {
        String message = "";
       // if (uploadService.checkIfDocumentFile(file))
            try {

                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return new ResponseEntity<>(testimonialService.uploadTestimonial(userId, file), HttpStatus.OK);
                //return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                log.info("Could not upload the file: Due to {}", e.getLocalizedMessage());
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }

        //message = "Please upload an DOC or PDF file!";
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }
}
