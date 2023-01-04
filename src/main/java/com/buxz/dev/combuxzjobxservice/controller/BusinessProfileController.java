package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.BusinessProfileDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.BusinessProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.BusinessCategory;
import com.buxz.dev.combuxzjobxservice.service.BusinessProfileService;
import com.buxz.dev.combuxzjobxservice.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/jobx/profile/business")
public class BusinessProfileController {

    @Autowired
    private BusinessProfileService businessProfileService;

    @Autowired
    private UploadService uploadService;

    @PostMapping
    public ResponseEntity<BusinessProfileEntity> createBusinessProfile(@RequestBody BusinessProfileDto businessProfileDto) {
        try {
            return new ResponseEntity<>(businessProfileService.createBusinessProfile(businessProfileDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<BusinessProfileEntity> updateBusinessProfile(@PathVariable("id") int id, @RequestBody BusinessProfileDto businessProfileDto) {
        Optional<BusinessProfileEntity> businessProfileToUpdate = businessProfileService.getBusinessProfileById(id);
        if(businessProfileToUpdate.isPresent()) {
            return new ResponseEntity<>(businessProfileService.updateBusinessProfile(id, businessProfileDto),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{busid}/business/upload")
    public ResponseEntity<ResponseMessage> uploadSupportingImages(@PathVariable("busid") int bid, @RequestParam("file") MultipartFile file) throws IOException {
        String message = "";
        Optional<BusinessProfileEntity> businessProfile = businessProfileService.getBusinessProfileById(bid);
        if (businessProfile.isPresent()) {
            if (uploadService.checkIfImageFile(file)) {
                try {
                    businessProfileService.addSupportingImages(bid, file);
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
        } else {
            message = "No business profile associated with bid" + bid;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(message));
        }

    }

    @GetMapping
    public ResponseEntity<List<BusinessProfileEntity>> getAllBusinessProfiles(@RequestParam(required = false) String businessProfile) {
        try {
            List<BusinessProfileEntity> businessProfileList;
            if(businessProfile == null) {
                businessProfileList = businessProfileService.getListOfAllBusinessProfiles();
            } else {
                businessProfileList = businessProfileService.getListOfBusinessProfilesContaining(businessProfile);
            }

            if (!businessProfileList.isEmpty()) {
                return new ResponseEntity<>(businessProfileList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Failed to list all business profiles : due to {}", e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category")
    public ResponseEntity<List<BusinessProfileEntity>> getBusinessProfilesByCategory(@RequestParam BusinessCategory businessCategory) {
        try {
            List<BusinessProfileEntity> profilesByCategory = businessProfileService.getBusinessProfilesByCategory(businessCategory);
            if (!profilesByCategory.isEmpty()) {
                return new ResponseEntity<>(profilesByCategory, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessProfileEntity> getBusinessById(@PathVariable("id") int id) {
        Optional<BusinessProfileEntity> businessProfile = businessProfileService.getBusinessProfileById(id);
        return businessProfile.map(businessProfileEntity -> new ResponseEntity<>(businessProfileEntity, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ResponseMessage> deactivateBusinessProfile(@PathVariable("id") int id) {
        Optional<BusinessProfileEntity> businessProfile = businessProfileService.getBusinessProfileById(id);
        if(businessProfile.isPresent()) {
            return new ResponseEntity<>(businessProfileService.deactivateBusinessProfile(id), HttpStatus.GONE);
        } else {
            return new ResponseEntity<>(new ResponseMessage("No User found for this profile"),HttpStatus.NOT_FOUND);
        }
    }

}
