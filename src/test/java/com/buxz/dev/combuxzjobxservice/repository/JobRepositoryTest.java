package com.buxz.dev.combuxzjobxservice.repository;

import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
