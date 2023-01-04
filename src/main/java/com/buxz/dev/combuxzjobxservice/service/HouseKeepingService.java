package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.repository.JobRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
public class HouseKeepingService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public HouseKeepingService(JobRepository jobRepository, UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public void updateJobEntriesToNew() {
        List<JobEntryEntity> jobEntryEntityList = jobRepository.findAllByJobState(JobCurrentState.CREATED.toString());
        if (jobEntryEntityList.isEmpty()) {
            log.info("UpdateJobTriggered: NO JobEntry in status CREATED currently");
        } else {
            jobEntryEntityList.forEach(jobEntryEntity -> {
                jobEntryEntity.setJobState(JobCurrentState.NEW);
                jobRepository.save(jobEntryEntity);
            });
            log.info("UpdateJobTriggered: All {} JobEntries in CREATED successfully updated to NEW", jobEntryEntityList.size());
        }
    }

    public void getAllJobEntryWithApproachingDueDate() {
        List<JobEntryEntity> publishedJobEntries = jobRepository.findAllByJobState(JobCurrentState.PUBLISHED.toString());

        if (!publishedJobEntries.isEmpty()) {
            publishedJobEntries.forEach(jobEntryEntity -> {
                if(closingDateDue(jobEntryEntity.getClosingDate())) {
                    jobEntryEntity.setJobState(JobCurrentState.CLOSING_DATE_DUE);
                    jobRepository.save(jobEntryEntity);
                    log.info("DueDateTask: Updated all Published JobEntries with - CLOSING DATE DUE");
                }
                if (closingDateOverDue(jobEntryEntity.getClosingDate())) {
                    jobEntryEntity.setJobState(JobCurrentState.CLOSING_DATE_OVERDUE);
                    jobRepository.save(jobEntryEntity);
                    log.info("DueDateTask: Updated all Published JobEntries with - CLOSING DATE OVERDUE");
                }
            });
        } else {
            log.info("DueDateTask: No Published JobEntries in the database");
        }
    }

    public void permanentlyDeleteJobEntries() {
        List<JobEntryEntity> jobEntriesToDelete = jobRepository.findAllByJobState(JobCurrentState.DELETED.toString());
        if(!jobEntriesToDelete.isEmpty()) {
            jobEntriesToDelete.forEach(jobRepository::delete);
            log.info("ScheduledTask: Permanently deleted JobEntries in RecyclingBin more than 30days");
        } else {
            log.info("ScheduledTask: NO JobEntries in RecyclingBin more than 30days to delete");
        }
    }

    public boolean closingDateDue(LocalDate closingDate) {
        LocalDate from = LocalDate.now();
        LocalDate to = closingDate;

        Period period = Period.between(from, to);
        return period.getMonths() == 0 && period.getDays() <= 7;
    }

    public boolean closingDateOverDue(LocalDate closingDate) {
        LocalDate from = LocalDate.now();
        LocalDate to = closingDate;

        Period period = Period.between(from, to);
        return period.isNegative();
    }

}
