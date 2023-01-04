package com.buxz.dev.combuxzjobxservice.repository;

import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntryEntity, Integer> {
    List<JobEntryEntity> findAllByJobTitleContaining(String jobTitle);
    @Query(
            value = "SELECT * FROM Job_Entry_Entity j WHERE j.job_state = ?",
            nativeQuery = true)
    List<JobEntryEntity> findAllByJobState(String jobState);
}
