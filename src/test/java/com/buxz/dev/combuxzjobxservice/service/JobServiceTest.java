package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.JobDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobType;
import com.buxz.dev.combuxzjobxservice.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    private JobDto jobDto;
    private JobEntryEntity jobEntryEntity;

    @BeforeEach
    void setUp() {
        jobDto = new JobDto();
        jobDto.setJobTitle("Software Engineer");
        jobDto.setJobDescription("Develop software");
        jobDto.setEmployer("Tech Corp");
        jobDto.setJobType(JobType.FULL_TIME);
        jobDto.setSalary("50000");
        jobDto.setJobCity("Cape Town");
        jobDto.setClosingDate(LocalDate.now().plusDays(5));

        jobEntryEntity = JobEntryEntity.builder()
                .id(1)
                .jobTitle("Existing Job")
                .jobDescription("Existing description")
                .employer("Existing Employer")
                .jobType(JobType.CONTRACT)
                .salary("40000")
                .jobCity("Johannesburg")
                .closingDate(LocalDate.now().plusDays(10))
                .jobState(JobCurrentState.CREATED)
                .published(false)
                .dateCreated(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllAvailableJobs_ShouldReturnAllJobs() {
        when(jobRepository.findAll()).thenReturn(List.of(jobEntryEntity));

        List<JobEntryEntity> result = jobService.getAllAvailableJobs();

        assertEquals(1, result.size());
        verify(jobRepository).findAll();
    }

    @Test
    void getAvailableJobsContaining_ShouldDelegateToRepository() {
        when(jobRepository.findAllByJobTitleContaining("Engineer")).thenReturn(List.of(jobEntryEntity));

        List<JobEntryEntity> result = jobService.getAvailableJobsContaining("Engineer");

        assertEquals(1, result.size());
        verify(jobRepository).findAllByJobTitleContaining("Engineer");
    }

    @Test
    void getJobById_ShouldReturnOptionalFromRepository() {
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntryEntity));

        Optional<JobEntryEntity> result = jobService.getJobById(1);

        assertTrue(result.isPresent());
        assertEquals(jobEntryEntity, result.get());
        verify(jobRepository).findById(1);
    }

    @Test
    void createNewJobEntry_ShouldPopulateAndSaveJob() {
        ArgumentCaptor<JobEntryEntity> captor = ArgumentCaptor.forClass(JobEntryEntity.class);

        JobEntryEntity result = jobService.createNewJobEntry(jobDto);

        verify(jobRepository).save(captor.capture());
        JobEntryEntity savedEntity = captor.getValue();

        assertEquals(jobDto.getJobTitle(), result.getJobTitle());
        assertEquals(jobDto.getEmployer(), result.getEmployer());
        assertEquals(JobCurrentState.CREATED, result.getJobState());
        assertNotNull(result.getDateCreated());
        assertEquals(savedEntity, result);
    }

    @Test
    void updateJobEntry_ShouldUpdateFieldsAndSave() {
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntryEntity));
        when(jobRepository.save(any(JobEntryEntity.class))).thenReturn(jobEntryEntity);

        JobEntryEntity result = jobService.updateJobEntry(1, jobDto);

        assertEquals(jobDto.getJobTitle(), result.getJobTitle());
        assertEquals(jobDto.getJobDescription(), result.getJobDescription());
        assertEquals(jobDto.getEmployer(), result.getEmployer());
        verify(jobRepository).save(jobEntryEntity);
    }

    @Test
    void deleteJobEntry_WhenJobExists_ShouldUpdateState() {
        jobEntryEntity.setJobState(JobCurrentState.NEW);
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntryEntity));

        ResponseMessage message = jobService.deleteJobEntry(1);

        assertEquals("Job Entry Deleted Successfully", message.getMessage());
        assertEquals(JobCurrentState.DELETED, jobEntryEntity.getJobState());
        verify(jobRepository).findById(1);
    }

    @Test
    void deleteJobEntry_WhenJobNotFound_ShouldReturnErrorMessage() {
        when(jobRepository.findById(1)).thenReturn(Optional.empty());

        ResponseMessage message = jobService.deleteJobEntry(1);

        assertEquals("No Job Entry found to be Deleted", message.getMessage());
    }

    @Test
    void publishJobEntry_WhenJobExists_ShouldUpdateStateAndPublish() {
        jobEntryEntity.setJobState(JobCurrentState.CREATED);
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntryEntity));

        ResponseMessage message = jobService.publishJobEntry(1);

        assertEquals("JobEntry Successfully Published", message.getMessage());
        assertEquals(JobCurrentState.PUBLISHED, jobEntryEntity.getJobState());
        assertTrue(jobEntryEntity.isPublished());
        verify(jobRepository).save(jobEntryEntity);
    }

    @Test
    void publishJobEntry_WhenJobNotFound_ShouldReturnNoJobMessage() {
        when(jobRepository.findById(1)).thenReturn(Optional.empty());

        ResponseMessage message = jobService.publishJobEntry(1);

        assertEquals("No JobEntry To Publish", message.getMessage());
        verify(jobRepository, never()).save(any());
    }

    @Test
    void publishJobEntry_WhenSaveFails_ShouldReturnFailureMessage() {
        jobEntryEntity.setJobState(JobCurrentState.CREATED);
        when(jobRepository.findById(1)).thenReturn(Optional.of(jobEntryEntity));
        when(jobRepository.save(any(JobEntryEntity.class))).thenThrow(new RuntimeException("boom"));

        ResponseMessage message = jobService.publishJobEntry(1);

        assertEquals("Failed to publish JobEntry", message.getMessage());
    }
}
