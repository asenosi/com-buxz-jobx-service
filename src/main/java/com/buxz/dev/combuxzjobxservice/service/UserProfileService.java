package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.UserProfileDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    private final UserAccountService userAccountService;
    private final UserRepository userRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, UserAccountService userAccountService, UserRepository userRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userAccountService = userAccountService;
        this.userRepository = userRepository;
    }

    public UserProfileEntity createUserProfile(int accountId, UserProfileDto userProfileDto) {
        Optional<UserAccountEntity> userAccount = userAccountService.getUserAccountById(accountId);
        UserProfileEntity userProfile = new UserProfileEntity();
        if (userAccount.isPresent()) {
            List<UserProfileEntity> accountUserProfiles = userAccount.get().getUserProfiles();
            userProfile.setId(userAccount.get().getId());
            userProfile.setShowProfile(userProfileDto.isShowProfile());
            userProfile.setCity(userProfileDto.getCity());
            userProfile.setJobTitle(userProfileDto.getJobTitle());
            userProfile.setProfileSummary(userProfileDto.getProfileSummary());
            userProfile.setContactDetails(userProfileDto.getContactDetails());
            userProfile.setDateOfBirth(userProfileDto.getDateOfBirth());
            userProfile.setProfileStatus(ProfileStatus.ACTIVE);
            userProfileRepository.save(userProfile);
            accountUserProfiles.add(userProfile);
            userAccount.get().setUserProfiles(accountUserProfiles);
            userRepository.save(userAccount.get());
            log.info("Created a new Profile for Username : TOADD");
        } else {
            log.info("NoUserAccount: No used account with ID: {} to link profile to ", accountId);
        }
        return userProfile;
    }

    public List<UserProfileEntity> getListOfAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    public Optional<UserProfileEntity> getUserProfileById(int userId) {
        return userProfileRepository.findById(userId);
    }

    public UserProfileEntity updateUserProfile(int id, UserProfileDto userProfileDto) {
        return new UserProfileEntity();
    }

    public ResponseMessage deactivateUserProfile(int id) {
        //TODO Find record by Status and Id
        //Optional<UserProfileEntity> profileToDeactivate = userProfileRepository.findByIdAndStatus(id, "ACTIVE");

        log.info("Received a request to Deactivate a User ProfileId: {}", id);
        Optional<UserProfileEntity> profileToDeactivate = userProfileRepository.findById(id);
        if (profileToDeactivate.isPresent()) {
            profileToDeactivate.get().setShowProfile(false);
            profileToDeactivate.get().setProfileStatus(ProfileStatus.DEACTIVATED);
            userProfileRepository.save(profileToDeactivate.get());
            return new ResponseMessage("Deactivated successfully");
        } else {
            return new ResponseMessage("Profile Already Deactivated or Suspended");
        }
    }

}
