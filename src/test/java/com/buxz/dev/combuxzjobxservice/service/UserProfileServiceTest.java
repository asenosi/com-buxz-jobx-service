package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.UserProfileDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ContactDetails;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserAccountService userAccountService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserProfileService userProfileService;

    private UserProfileDto userProfileDto;
    private UserAccountEntity userAccount;

    @BeforeEach
    void setUp() {
        userProfileDto = new UserProfileDto();
        userProfileDto.setJobTitle("Developer");
        userProfileDto.setProfileSummary("Writes code");
        userProfileDto.setCity("Cape Town");
        userProfileDto.setDateOfBirth(LocalDate.of(1992, 6, 15));
        userProfileDto.setContactDetails(ContactDetails.builder().email("profile@example.com").build());

        userAccount = new UserAccountEntity();
        userAccount.setId(100);
        userAccount.setUserProfiles(new ArrayList<>());
    }

    @Test
    void createUserProfile_WhenAccountExists_ShouldCreateAndLinkProfile() {
        when(userAccountService.getUserAccountById(100)).thenReturn(Optional.of(userAccount));
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(UserAccountEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileEntity result = userProfileService.createUserProfile(100, userProfileDto);

        assertEquals(100, result.getId());
        assertEquals(userProfileDto.getJobTitle(), result.getJobTitle());
        assertEquals(userProfileDto.getProfileSummary(), result.getProfileSummary());
        assertEquals(ProfileStatus.ACTIVE, result.getProfileStatus());
        assertTrue(result.isShowProfile());
        assertEquals(1, userAccount.getUserProfiles().size());
        verify(userProfileRepository).save(result);
        verify(userRepository).save(userAccount);
    }

    @Test
    void createUserProfile_WhenAccountMissing_ShouldReturnEmptyProfile() {
        when(userAccountService.getUserAccountById(100)).thenReturn(Optional.empty());

        UserProfileEntity result = userProfileService.createUserProfile(100, userProfileDto);

        assertNull(result.getJobTitle());
        verify(userProfileRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void getListOfAllUserProfiles_ShouldReturnAllProfiles() {
        UserProfileEntity profile = new UserProfileEntity();
        when(userProfileRepository.findAll()).thenReturn(List.of(profile));

        List<UserProfileEntity> result = userProfileService.getListOfAllUserProfiles();

        assertEquals(1, result.size());
        verify(userProfileRepository).findAll();
    }

    @Test
    void getUserProfileById_ShouldReturnOptional() {
        UserProfileEntity profile = new UserProfileEntity();
        when(userProfileRepository.findById(5)).thenReturn(Optional.of(profile));

        Optional<UserProfileEntity> result = userProfileService.getUserProfileById(5);

        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }

    @Test
    void deactivateUserProfile_WhenProfileExists_ShouldUpdateStatus() {
        UserProfileEntity profile = new UserProfileEntity();
        profile.setShowProfile(true);
        profile.setProfileStatus(ProfileStatus.ACTIVE);
        when(userProfileRepository.findById(5)).thenReturn(Optional.of(profile));
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenReturn(profile);

        ResponseMessage message = userProfileService.deactivateUserProfile(5);

        assertEquals("Deactivated successfully", message.getMessage());
        assertFalse(profile.isShowProfile());
        assertEquals(ProfileStatus.DEACTIVATED, profile.getProfileStatus());
        verify(userProfileRepository).save(profile);
    }

    @Test
    void deactivateUserProfile_WhenProfileMissing_ShouldReturnAlreadyMessage() {
        when(userProfileRepository.findById(5)).thenReturn(Optional.empty());

        ResponseMessage message = userProfileService.deactivateUserProfile(5);

        assertEquals("Profile Already Deactivated or Suspended", message.getMessage());
        verify(userProfileRepository, never()).save(any());
    }
}
