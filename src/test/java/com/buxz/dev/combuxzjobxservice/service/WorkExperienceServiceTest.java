package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.WorkExperienceDto;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.WorkExperienceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkExperienceServiceTest {

    @Mock
    private WorkExperienceRepository workExperienceRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private WorkExperienceService workExperienceService;

    private WorkExperienceDto workExperienceDto;
    private UserProfileEntity userProfile;

    @BeforeEach
    void setUp() {
        workExperienceDto = new WorkExperienceDto();
        workExperienceDto.setEmployer("Company");
        workExperienceDto.setExperienceJobTitle("Engineer");
        workExperienceDto.setJobDescription("Did things");
        workExperienceDto.setStartDate("2020-01-01");
        workExperienceDto.setEndDate("2021-01-01");
        workExperienceDto.setStillWorksHere(false);

        userProfile = new UserProfileEntity();
        userProfile.setId(1);
        userProfile.setWorkExperienceEntity(new ArrayList<>());
    }

    @Test
    void addWorkExperience_WhenProfileExists_ShouldPersistExperience() {
        when(userProfileRepository.findById(1)).thenReturn(Optional.of(userProfile));
        when(workExperienceRepository.saveAndFlush(any(WorkExperienceEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userProfileRepository.save(any(UserProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        WorkExperienceEntity result = workExperienceService.addWorkExperience(1, workExperienceDto);

        assertEquals(workExperienceDto.getEmployer(), result.getEmployer());
        assertEquals(workExperienceDto.getExperienceJobTitle(), result.getExperienceJobTitle());
        assertEquals(1, userProfile.getWorkExperienceEntity().size());
        verify(workExperienceRepository).saveAndFlush(result);
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    void addWorkExperience_WhenProfileMissing_ShouldReturnEmptyEntity() {
        when(userProfileRepository.findById(1)).thenReturn(Optional.empty());

        WorkExperienceEntity result = workExperienceService.addWorkExperience(1, workExperienceDto);

        assertNull(result.getEmployer());
        verify(workExperienceRepository, never()).saveAndFlush(any());
    }

    @Test
    void getWorkExperienceById_ShouldReturnOptional() {
        WorkExperienceEntity entity = new WorkExperienceEntity();
        when(workExperienceRepository.findById(5)).thenReturn(Optional.of(entity));

        Optional<WorkExperienceEntity> result = workExperienceService.getWorkExperienceById(5);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
    }

    @Test
    void updateWorkExperience_WhenExperienceExists_ShouldUpdateFields() {
        WorkExperienceEntity existing = new WorkExperienceEntity();
        when(workExperienceRepository.findById(2)).thenReturn(Optional.of(existing));
        when(workExperienceRepository.save(any(WorkExperienceEntity.class))).thenReturn(existing);

        WorkExperienceEntity result = workExperienceService.updateWorkExperience(1, 2, workExperienceDto);

        assertEquals(workExperienceDto.getEmployer(), result.getEmployer());
        assertEquals(workExperienceDto.getExperienceJobTitle(), result.getExperienceJobTitle());
        assertEquals(workExperienceDto.getJobDescription(), result.getJobDescription());
        verify(workExperienceRepository).save(existing);
    }

    @Test
    void updateWorkExperience_WhenExperienceMissing_ShouldThrowException() {
        when(workExperienceRepository.findById(3)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> workExperienceService.updateWorkExperience(1, 3, workExperienceDto));
    }

    @Test
    void deleteWorkExperience_ShouldInvokeRepositoryDelete() {
        ResponseMessage message = workExperienceService.deleteWorkExperience(1, 4);

        assertEquals("DeleteWorkExperience : Work experience deleted successfully", message.getMessage());
        verify(workExperienceRepository).deleteById(4);
    }
}
