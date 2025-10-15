package com.buxz.dev.combuxzjobxservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.buxz.dev.combuxzjobxservice.domain.EducationDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.EducationEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.LevelOfEducation;
import com.buxz.dev.combuxzjobxservice.repository.EducationRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EducationServiceTest {

    @Mock
    private EducationRepository educationRepository;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private EducationService educationService;

    private EducationDto testEducationDto;
    private EducationEntity testEducationEntity;
    private UserProfileEntity testUserProfile;
    private final int TEST_USER_ID = 1;
    private final int TEST_EDUCATION_ID = 1;

    @BeforeEach
    void setUp() {
        testEducationDto = EducationDto.builder()
                .levelOfEducation(LevelOfEducation.DIPLOMA)
                .school("Test University")
                .currentlyEnrolled(true)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2024, 1, 1))
                .visible(true)
                .build();

        testEducationEntity = EducationEntity.builder()
                .id(TEST_EDUCATION_ID)
                .levelOfEducation(LevelOfEducation.DIPLOMA)
                .school("Test University")
                .currentlyEnrolled(true)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2024, 1, 1))
                .visible(true)
                .build();

        testUserProfile = UserProfileEntity.builder()
                .id(TEST_USER_ID)
                .educationHistory(new ArrayList<>())
                .build();
    }

    @Test
    void addEducationEntry_WhenUserProfileExists_ShouldAddEducationAndUpdateUserProfile() {
        // Arrange
        when(userProfileService.getUserProfileById(TEST_USER_ID))
                .thenReturn(Optional.of(testUserProfile));
        when(educationRepository.save(any(EducationEntity.class)))
                .thenReturn(testEducationEntity);

        // Act
        EducationEntity result = educationService.addEducationEntry(TEST_USER_ID, testEducationDto);

        // Assert
        assertNotNull(result);
        assertEquals(testEducationDto.getLevelOfEducation(), result.getLevelOfEducation());
        assertEquals(testEducationDto.getSchool(), result.getSchool());
        assertEquals(testEducationDto.isCurrentlyEnrolled(), result.isCurrentlyEnrolled());
        assertEquals(testEducationDto.isVisible(), result.isVisible());

        // Verify user profile was updated with new education entry
        assertTrue(testUserProfile.getEducationHistory().contains(result));
        verify(userProfileService, times(1)).getUserProfileById(TEST_USER_ID);
        verify(educationRepository, times(1)).save(any(EducationEntity.class));
    }

    @Test
    void addEducationEntry_WhenUserProfileNotExists_ShouldThrowException() {
        // Arrange
        when(userProfileService.getUserProfileById(TEST_USER_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            educationService.addEducationEntry(TEST_USER_ID, testEducationDto);
        });

        verify(userProfileService, times(1)).getUserProfileById(TEST_USER_ID);
        verify(educationRepository, never()).save(any(EducationEntity.class));
    }

    @Test
    void addEducationEntry_ShouldMaintainExistingEducationHistory() {
        // Arrange
        EducationEntity existingEducation = EducationEntity.builder()
                .id(2)
                .levelOfEducation(LevelOfEducation.HIGH_SCHOOL)
                .school("Test High School")
                .build();
        testUserProfile.setEducationHistory(new ArrayList<>(List.of(existingEducation)));

        when(userProfileService.getUserProfileById(TEST_USER_ID))
                .thenReturn(Optional.of(testUserProfile));
        when(educationRepository.save(any(EducationEntity.class)))
                .thenReturn(testEducationEntity);

        // Act
        EducationEntity result = educationService.addEducationEntry(TEST_USER_ID, testEducationDto);

        // Assert
        assertEquals(2, testUserProfile.getEducationHistory().size());
        assertTrue(testUserProfile.getEducationHistory().contains(existingEducation));
        assertTrue(testUserProfile.getEducationHistory().contains(result));
        verify(educationRepository, times(1)).save(any(EducationEntity.class));
    }

    @Test
    void getListOfEducationHistory_ShouldReturnAllEducationEntries() {
        // Arrange
        List<EducationEntity> expectedEducationList = List.of(testEducationEntity);
        when(educationRepository.findAll()).thenReturn(expectedEducationList);

        // Act
        List<EducationEntity> result = educationService.getListOfEducationHistory();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEducationEntity, result.get(0));
        verify(educationRepository, times(1)).findAll();
    }

    @Test
    void getListOfEducationHistory_WhenNoEntries_ShouldReturnEmptyList() {
        // Arrange
        when(educationRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<EducationEntity> result = educationService.getListOfEducationHistory();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(educationRepository, times(1)).findAll();
    }

    @Test
    void getListOfVisibleEducationHistory_ShouldReturnOnlyVisibleEntries() {
        // Arrange
        EducationEntity hiddenEducation = EducationEntity.builder()
                .id(2)
                .levelOfEducation(LevelOfEducation.DIPLOMA)
                .school("Another University")
                .visible(false)
                .build();

        List<EducationEntity> allEducation = List.of(testEducationEntity, hiddenEducation);
        when(educationRepository.findAll()).thenReturn(allEducation);

        // Act
        List<EducationEntity> result = educationService.getListOfVisibleEducationHistory(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isVisible());
        assertEquals(testEducationEntity, result.get(0));
        verify(educationRepository, times(1)).findAll();
    }

    @Test
    void getListOfVisibleEducationHistory_WhenAllHidden_ShouldReturnEmptyList() {
        // Arrange
        EducationEntity hiddenEducation1 = EducationEntity.builder().id(1).visible(false).build();
        EducationEntity hiddenEducation2 = EducationEntity.builder().id(2).visible(false).build();

        List<EducationEntity> allEducation = List.of(hiddenEducation1, hiddenEducation2);
        when(educationRepository.findAll()).thenReturn(allEducation);

        // Act
        List<EducationEntity> result = educationService.getListOfVisibleEducationHistory(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(educationRepository, times(1)).findAll();
    }

    @Test
    void updateEducationItem_WhenUserProfileAndEducationExist_ShouldUpdateSuccessfully() {
        // Arrange
        EducationDto updateDto = EducationDto.builder()
                .levelOfEducation(LevelOfEducation.DIPLOMA)
                .school("Updated University")
                .currentlyEnrolled(false)
                .startDate(LocalDate.of(2024, 2, 1))
                .endDate(LocalDate.of(2026, 2, 1))
                .visible(false)
                .build();

        when(userProfileService.getUserProfileById(TEST_USER_ID))
                .thenReturn(Optional.of(testUserProfile));
        when(educationRepository.findById(TEST_EDUCATION_ID))
                .thenReturn(Optional.of(testEducationEntity));
        when(educationRepository.save(any(EducationEntity.class)))
                .thenReturn(testEducationEntity);

        // Act
        EducationEntity result = educationService.updateEducationItem(TEST_EDUCATION_ID, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updateDto.getLevelOfEducation(), result.getLevelOfEducation());
        assertEquals(updateDto.getSchool(), result.getSchool());
        assertEquals(updateDto.isCurrentlyEnrolled(), result.isCurrentlyEnrolled());
        assertEquals(updateDto.isVisible(), result.isVisible());

        // Verify user profile was updated
        assertTrue(testUserProfile.getEducationHistory().contains(result));
        verify(userProfileService, times(1)).getUserProfileById(TEST_USER_ID);
        verify(educationRepository, times(1)).findById(TEST_EDUCATION_ID);
        verify(educationRepository, times(1)).save(testEducationEntity);
    }

    @Test
    void updateEducationItem_WhenUserProfileNotExists_ShouldThrowException() {
        // Arrange
        when(userProfileService.getUserProfileById(TEST_USER_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            educationService.updateEducationItem(TEST_EDUCATION_ID, testEducationDto);
        });

        verify(userProfileService, times(1)).getUserProfileById(TEST_USER_ID);
        verify(educationRepository, never()).findById(anyInt());
        verify(educationRepository, never()).save(any(EducationEntity.class));
    }

    @Test
    void updateEducationItem_WhenEducationNotExists_ShouldThrowException() {
        // Arrange
        when(userProfileService.getUserProfileById(TEST_USER_ID))
                .thenReturn(Optional.of(testUserProfile));
        when(educationRepository.findById(TEST_EDUCATION_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            educationService.updateEducationItem(TEST_EDUCATION_ID, testEducationDto);
        });

        verify(userProfileService, times(1)).getUserProfileById(TEST_USER_ID);
        verify(educationRepository, times(1)).findById(TEST_EDUCATION_ID);
        verify(educationRepository, never()).save(any(EducationEntity.class));
    }

    @Test
    void toggleHideShowEducationEntry_ShouldToggleVisibility() {
        // Arrange
        boolean initialVisibility = testEducationEntity.isVisible();

        when(educationRepository.getById(TEST_EDUCATION_ID))
                .thenReturn(testEducationEntity);
        when(educationRepository.save(any(EducationEntity.class)))
                .thenReturn(testEducationEntity);

        // Act
        ResponseMessage result = educationService.toggleHideShowEducationEntry(TEST_EDUCATION_ID);

        // Assert
        assertNotNull(result);
        assertEquals("Education Entry Toggled to Hide/Show", result.getMessage());
        assertEquals(!initialVisibility, testEducationEntity.isVisible());
        verify(educationRepository, times(1)).getById(TEST_EDUCATION_ID);
        verify(educationRepository, times(1)).save(testEducationEntity);
    }

    @Test
    void toggleHideShowEducationEntry_WhenMultipleCalls_ShouldToggleCorrectly() {
        // Arrange
        testEducationEntity.setVisible(true);

        when(educationRepository.getById(TEST_EDUCATION_ID))
                .thenReturn(testEducationEntity);
        when(educationRepository.save(any(EducationEntity.class)))
                .thenReturn(testEducationEntity);

        // Act - First call
        educationService.toggleHideShowEducationEntry(TEST_EDUCATION_ID);

        // Assert - First call
        assertFalse(testEducationEntity.isVisible());

        // Act - Second call
        educationService.toggleHideShowEducationEntry(TEST_EDUCATION_ID);

        // Assert - Second call
        assertTrue(testEducationEntity.isVisible());

        verify(educationRepository, times(2)).getById(TEST_EDUCATION_ID);
        verify(educationRepository, times(2)).save(testEducationEntity);
    }

    @Test
    void updateEducationItem_ShouldAddUpdatedEducationToUserProfileHistory() {
        // Arrange
        EducationEntity existingEducation = EducationEntity.builder()
                .id(2)
                .levelOfEducation(LevelOfEducation.ABET)
                .build();
        testUserProfile.setEducationHistory(new ArrayList<>(List.of(existingEducation)));

        when(userProfileService.getUserProfileById(TEST_USER_ID))
                .thenReturn(Optional.of(testUserProfile));
        when(educationRepository.findById(TEST_EDUCATION_ID))
                .thenReturn(Optional.of(testEducationEntity));
        when(educationRepository.save(any(EducationEntity.class)))
                .thenReturn(testEducationEntity);

        // Act
        EducationEntity result = educationService.updateEducationItem(TEST_EDUCATION_ID, testEducationDto);

        // Assert
        assertEquals(2, testUserProfile.getEducationHistory().size());
        assertTrue(testUserProfile.getEducationHistory().contains(existingEducation));
        assertTrue(testUserProfile.getEducationHistory().contains(result));
    }
}