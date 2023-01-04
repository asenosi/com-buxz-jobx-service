package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.JobDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JobService {

    @Autowired JobRepository jobRepository;

    public List<JobEntryEntity> getAllAvailableJobs() {
        return jobRepository.findAll();
    }

    public List<JobEntryEntity> getAvailableJobsContaining(String jobTitle) {
        log.info("GetJobEntriesByJobTitle : Attempting to retrieve JobEntries containing -- {}", jobTitle);
        return jobRepository.findAllByJobTitleContaining(jobTitle);
    }

    public Optional<JobEntryEntity> getJobById(int id) {
        log.info("GetJobEntryById : Attempting to retrieve JobEntry by Id");
        return jobRepository.findById(id);
    }

    public JobEntryEntity createNewJobEntry(JobDto jobDto) {
        JobEntryEntity newJobEntry = new JobEntryEntity();
        newJobEntry.setJobTitle(jobDto.getJobTitle());
        newJobEntry.setJobDescription(jobDto.getJobDescription());
        newJobEntry.setJobCity(jobDto.getJobCity());
        newJobEntry.setEmployer(jobDto.getEmployer());
        newJobEntry.setSalary(jobDto.getSalary());
        newJobEntry.setJobType(jobDto.getJobType());
        newJobEntry.setClosingDate(jobDto.getClosingDate());
        newJobEntry.setJobState(JobCurrentState.CREATED);
        newJobEntry.setDateCreated(LocalDateTime.now());
        jobRepository.save(newJobEntry);
        log.info("CreatedNewJobEntry : {} Created a new Job Entry", jobDto.getEmployer());
        return newJobEntry;
    }

    public JobEntryEntity updateJobEntry(int id, JobDto jobDto) {
        JobEntryEntity jobToUpdate = jobRepository.findById(id).get();
        jobToUpdate.setJobTitle(jobDto.getJobTitle());
        jobToUpdate.setJobDescription(jobDto.getJobDescription());
        jobToUpdate.setJobCity(jobDto.getJobCity());
        jobToUpdate.setEmployer(jobDto.getEmployer());
        jobToUpdate.setSalary(jobDto.getSalary());
        jobToUpdate.setJobType(jobDto.getJobType());
        jobRepository.save(jobToUpdate);
        log.info("UpdatedJobEntry : {} Updated a Job Entry for {} ", jobDto.getEmployer(), jobDto.getJobTitle());
        return jobToUpdate;
    }

    public ResponseMessage deleteJobEntry(int id) {
        Optional<JobEntryEntity> jobToUpdate = jobRepository.findById(id);
        if (jobToUpdate.isPresent()) {
            jobToUpdate.get().setJobState(JobCurrentState.DELETED);
            log.info("DeleteJobEntry : {} Deleted a Job Entry for {} ", jobToUpdate.get().getEmployer(), jobToUpdate.get().getJobTitle());
            return new ResponseMessage("Job Entry Deleted Successfully");
        } else {
            log.info("DeleteJobEntry : No Job Entry found to be Deleted");
            return new ResponseMessage("No Job Entry found to be Deleted");
        }
    }

    public ResponseMessage publishJobEntry(int id) {
        Optional<JobEntryEntity> jobToPublish = jobRepository.findById(id);
        try {
            if (jobToPublish.isPresent()) {
                jobToPublish.get().setJobState(JobCurrentState.PUBLISHED);
                jobToPublish.get().setPublished(true);
                jobRepository.save(jobToPublish.get());
                log.info("PublishJobEntry : {} Published a Job Entry for {} ", jobToPublish.get().getEmployer(), jobToPublish.get().getJobTitle());
                return new ResponseMessage("JobEntry Successfully Published");
            } else {
                return new ResponseMessage("No JobEntry To Publish");
            }
        } catch (Exception e) {
            log.error("Failed to publish JobEntry | Due to {}", e.getLocalizedMessage());
            return new ResponseMessage("Failed to publish JobEntry");
        }
    }
}
