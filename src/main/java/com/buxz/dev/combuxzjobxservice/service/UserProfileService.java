package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.UserProfileDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserRepository;
import com.buxz.dev.combuxzjobxservice.mapper.UserProfileMapper;
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
    private final UserProfileMapper userProfileMapper;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository,
                              UserAccountService userAccountService,
                              UserRepository userRepository,
                              UserProfileMapper userProfileMapper) {
        this.userProfileRepository = userProfileRepository;
        this.userAccountService = userAccountService;
        this.userRepository = userRepository;
        this.userProfileMapper = userProfileMapper;
    }

    public UserProfileEntity createUserProfile(int accountId, UserProfileDto userProfileDto) {
        Optional<UserAccountEntity> userAccount = userAccountService.getUserAccountById(accountId);
        UserProfileEntity userProfile = userProfileMapper.toEntity(userProfileDto);
        if (userAccount.isPresent()) {
            List<UserProfileEntity> accountUserProfiles = userAccount.get().getUserProfiles();
            userProfile.setId(userAccount.get().getId());
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
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findById(id);
        if (userProfileEntity.isPresent()) {
            userProfileMapper.updateUserProfileFromDto(userProfileDto, userProfileEntity.get());
            userProfileRepository.save(userProfileEntity.get());
            log.info("UpdateUserProfile : Successfully updated profile with Id : {}", id);
            return userProfileEntity.get();
        } else {
            log.warn("UpdateUserProfile : Failed to update profile with Id : {} - NOT_FOUND", id);
            return new UserProfileEntity();
        }
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
