package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.UserProfileDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.service.UserAccountService;
import com.buxz.dev.combuxzjobxservice.service.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/v1/jobx/profile")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserAccountService userAccountService;

    @PostMapping("/{accountid}/create")
    public ResponseEntity<UserProfileEntity> createUserProfile(@PathVariable("accountid") int accountid,
            @RequestBody UserProfileDto userProfileDto) {
        log.info("Request to create user profile received, jobTitle: {} AccountId: {}", userProfileDto.getJobTitle(), accountid);
        Optional<UserAccountEntity> userAccount = userAccountService.getUserAccountById(accountid);
        if (userAccount.isPresent()) {
            try {
                return new ResponseEntity<>(userProfileService.createUserProfile(accountid, userProfileDto), HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.warn("ProfileNotCreated: No UserAccount linked to this profile: {}", accountid);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<UserProfileEntity> updateUserProfile(@PathVariable("id") int id, @RequestBody UserProfileDto userProfileDto) {
        Optional<UserProfileEntity> userProfileToUpdate = userProfileService.getUserProfileById(id);
        if(userProfileToUpdate.isPresent()) {
            return new ResponseEntity<>(userProfileService.updateUserProfile(id, userProfileDto),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserProfileEntity>> getAllUserProfiles() {
        try {
            List<UserProfileEntity> userProfileList = userProfileService.getListOfAllUserProfiles();
            if (!userProfileList.isEmpty()) {
                return new ResponseEntity<>(userProfileList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("GetAllUserProfiles: Failed to list all user profiles : {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileEntity> getUserById(@PathVariable("id") int id) {
        Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(id);
        if(userProfile.isPresent()) {
            return new ResponseEntity<>(userProfile.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ResponseMessage> deactivateUserProfile(@PathVariable("id") int id) {
        Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(id);
        if(userProfile.isPresent()) {
            return new ResponseEntity<>(userProfileService.deactivateUserProfile(id), HttpStatus.GONE);
        } else {
            return new ResponseEntity<>(new ResponseMessage("No User found for this profile"),HttpStatus.NOT_FOUND);
        }
    }
}
