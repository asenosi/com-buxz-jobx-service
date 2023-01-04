package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.JobDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/v1/jobx/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @GetMapping("/all")
    public ResponseEntity<List<JobEntryEntity>> getListOfAllJobs(@RequestParam(required = false) String jobTitle) {

        try {
            if (jobTitle == null) {
                return new ResponseEntity<>(jobService.getAllAvailableJobs(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(jobService.getAvailableJobsContaining(jobTitle), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<JobEntryEntity>> getJobById(@PathVariable("id") int id) {
        Optional<JobEntryEntity> job = jobService.getJobById(id);

        if (job.isPresent()) {
            return new ResponseEntity<>(jobService.getJobById(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    private ResponseEntity<JobEntryEntity> createNewJobEntry(@RequestBody JobDto jobDto) {
        try {
            return new ResponseEntity<>(jobService.createNewJobEntry(jobDto), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update")
    private ResponseEntity<JobEntryEntity> updateJobEntry (@PathVariable("id") int id, @RequestBody JobDto jobDto) {
        Optional<JobEntryEntity> jobToUpdate = jobService.getJobById(id);
        if (jobToUpdate.isPresent()) {
            return new ResponseEntity<>(jobService.updateJobEntry(id, jobDto), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}/delete")
    private ResponseEntity<ResponseMessage> deleteJobEntry(@PathVariable("id") int id) {
        Optional<JobEntryEntity> jobToDelete = jobService.getJobById(id);
        if (jobToDelete.isPresent()) {
            return new ResponseEntity<>(jobService.deleteJobEntry(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<ResponseMessage> publishJobEntry(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(jobService.publishJobEntry(id), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("Could not publish Job Entry: Try Later"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
