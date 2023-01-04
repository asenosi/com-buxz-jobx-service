package com.buxz.dev.combuxzjobxservice.controller;

import com.buxz.dev.combuxzjobxservice.domain.EducationDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.EducationEntity;
import com.buxz.dev.combuxzjobxservice.service.EducationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/jobx/profile/education")
public class EducationController {

    @Autowired EducationService educationService;

    @GetMapping("/{id}/all")
    private ResponseEntity<List<EducationEntity>> getAllEducationHistory(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(educationService.getListOfEducationHistory(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to RetrieveEducationHistory entry due to : {}", e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/visible")
    private ResponseEntity<List<EducationEntity>> getAllVisibleEducationHistory(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(educationService.getListOfVisibleEducationHistory(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to ShowVisibleEducation entry due to : {}", e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/add")
    private ResponseEntity<EducationEntity> addEducationEntry(@PathVariable("id") int id, @RequestBody EducationDto educationDto) {
        try {
            return new ResponseEntity<>(educationService.addEducationEntry(id, educationDto), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Failed to AddEducation entry due to : {}", e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/update")
    private ResponseEntity<EducationEntity> updateEducationItem(@PathVariable("id") int id, @RequestBody EducationDto educationDto) {
        try {
            return new ResponseEntity<>(educationService.updateEducationItem(id, educationDto), HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.error("Failed to UpdateEducation entry due to : {}", e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/hide")
    private ResponseEntity<ResponseMessage> hideEducationEntry(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<>(educationService.toggleHideShowEducationEntry(id), HttpStatus.MOVED_PERMANENTLY);
        } catch (Exception e) {
            log.error("Failed to HideEducation entry due to : {}", e.getLocalizedMessage());
            return new ResponseEntity<>(new ResponseMessage("Failed to HideEducation entry"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
