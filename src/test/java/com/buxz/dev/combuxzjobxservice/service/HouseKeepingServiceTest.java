package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.repository.JobRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HouseKeepingServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private HouseKeepingService houseKeepingService;

    private JobEntryEntity createdJob;
    private JobEntryEntity publishedDueJob;
    private JobEntryEntity publishedOverdueJob;

    @BeforeEach
    void setUp() {
        createdJob = JobEntryEntity.builder()
                .id(1)
                .jobState(JobCurrentState.CREATED)
                .build();

        publishedDueJob = JobEntryEntity.builder()
                .id(2)
                .jobState(JobCurrentState.PUBLISHED)
                .closingDate(LocalDate.now().plusDays(3))
                .build();

        publishedOverdueJob = JobEntryEntity.builder()
                .id(3)
                .jobState(JobCurrentState.PUBLISHED)
                .closingDate(LocalDate.now().minusDays(1))
                .build();
    }

    @Test
    void updateJobEntriesToNew_WhenCreatedJobsExist_ShouldUpdateState() {
        when(jobRepository.findAllByJobState(JobCurrentState.CREATED.toString())).thenReturn(List.of(createdJob));
        when(jobRepository.save(any(JobEntryEntity.class))).thenReturn(createdJob);

        houseKeepingService.updateJobEntriesToNew();

        assertEquals(JobCurrentState.NEW, createdJob.getJobState());
        verify(jobRepository).save(createdJob);
    }

    @Test
    void updateJobEntriesToNew_WhenNoJobs_ShouldNotSave() {
        when(jobRepository.findAllByJobState(JobCurrentState.CREATED.toString())).thenReturn(List.of());

        houseKeepingService.updateJobEntriesToNew();

        verify(jobRepository, never()).save(any());
    }

    @Test
    void getAllJobEntryWithApproachingDueDate_ShouldUpdateDueAndOverdueStates() {
        when(jobRepository.findAllByJobState(JobCurrentState.PUBLISHED.toString()))
                .thenReturn(List.of(publishedDueJob, publishedOverdueJob));
        when(jobRepository.save(any(JobEntryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        houseKeepingService.getAllJobEntryWithApproachingDueDate();

        assertEquals(JobCurrentState.CLOSING_DATE_DUE, publishedDueJob.getJobState());
        assertEquals(JobCurrentState.CLOSING_DATE_OVERDUE, publishedOverdueJob.getJobState());
        verify(jobRepository, times(3)).save(any(JobEntryEntity.class));
    }

    @Test
    void getAllJobEntryWithApproachingDueDate_WhenNoPublishedJobs_ShouldNotSave() {
        when(jobRepository.findAllByJobState(JobCurrentState.PUBLISHED.toString())).thenReturn(List.of());

        houseKeepingService.getAllJobEntryWithApproachingDueDate();

        verify(jobRepository, never()).save(any());
    }

    @Test
    void permanentlyDeleteJobEntries_WhenDeletedJobsExist_ShouldDeleteAll() {
        JobEntryEntity deleted = JobEntryEntity.builder().id(4).jobState(JobCurrentState.DELETED).build();
        when(jobRepository.findAllByJobState(JobCurrentState.DELETED.toString())).thenReturn(List.of(deleted));

        houseKeepingService.permanentlyDeleteJobEntries();

        verify(jobRepository).delete(deleted);
    }

    @Test
    void permanentlyDeleteJobEntries_WhenNoDeletedJobs_ShouldNotDelete() {
        when(jobRepository.findAllByJobState(JobCurrentState.DELETED.toString())).thenReturn(List.of());

        houseKeepingService.permanentlyDeleteJobEntries();

        verify(jobRepository, never()).delete(any());
    }

    @Test
    void closingDateDue_ShouldReturnTrueWhenWithinSevenDays() {
        LocalDate closingDate = LocalDate.now().plusDays(5);

        assertTrue(houseKeepingService.closingDateDue(closingDate));
    }

    @Test
    void closingDateDue_ShouldReturnFalseWhenMoreThanSevenDays() {
        LocalDate closingDate = LocalDate.now().plusDays(10);

        assertFalse(houseKeepingService.closingDateDue(closingDate));
    }

    @Test
    void closingDateOverDue_ShouldReturnTrueWhenPastDate() {
        assertTrue(houseKeepingService.closingDateOverDue(LocalDate.now().minusDays(2)));
    }

    @Test
    void closingDateOverDue_ShouldReturnFalseWhenFutureDate() {
        assertFalse(houseKeepingService.closingDateOverDue(LocalDate.now().plusDays(1)));
    }
}
