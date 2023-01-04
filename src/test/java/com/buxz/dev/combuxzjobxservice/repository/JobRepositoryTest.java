package com.buxz.dev.combuxzjobxservice.repository;

import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JobRepositoryTest {

    @Autowired
    JobRepository jobRepository;

    @Test
    private void checkIfAllJobsAreReturnByJobState() {
        //Given
        JobEntryEntity jobEntry = new JobEntryEntity().builder()
                .closingDate(LocalDate.now().plusDays(2))
                .dateCreated(LocalDateTime.now())
                .employer("JobX")
                .jobCity("Joburg")
                .jobDescription("Job-iJob")
                .jobTitle("Java Developer")
                .jobState(JobCurrentState.CREATED)
                .salary("2 Tawa")
                .build();
        jobRepository.save(jobEntry);

        //When
        List<JobEntryEntity> expected = jobRepository.findAllByJobState(JobCurrentState.CREATED.name());

        //Then
        assertThat(expected).asList();
    }
}
