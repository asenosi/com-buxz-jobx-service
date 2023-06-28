package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.EducationDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.EducationEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.repository.EducationRepository;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final UserProfileService userProfileService;

    @Autowired
    public EducationService(final EducationRepository educationRepository,
                            final UserProfileService userProfileService) {
        this.educationRepository = educationRepository;
        this.userProfileService = userProfileService;
    }

    public EducationEntity addEducationEntry(int id, EducationDto educationDto) {
        EducationEntity educationEntry = new EducationEntity();
        Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(id); //To Change
        List<EducationEntity> currentEducationHistory = userProfile.get().getEducationHistory();
        educationEntry.setLevelOfEducation(educationDto.getLevelOfEducation());
        educationEntry.setSchool(educationDto.getSchool());
        educationEntry.setCurrentlyEnrolled(educationDto.isCurrentlyEnrolled());
        educationEntry.setStartDate(educationDto.getStartDate());
        educationEntry.setEndDate(educationDto.getEndDate());
        educationEntry.setVisible(educationDto.isVisible());
        currentEducationHistory.add(educationEntry);
        userProfile.get().setEducationHistory(currentEducationHistory);
        educationRepository.save(educationEntry);
        log.info("AddEducationEntry : Successfully added EducationEntry to UserProfile with Id : {}", userProfile.get().getId());
        return educationEntry;
    }

    public List<EducationEntity> getListOfEducationHistory() {
        return educationRepository.findAll();
    }

    public List<EducationEntity> getListOfVisibleEducationHistory(int id) {
        return educationRepository.findAll().stream()
                .filter(EducationEntity::isVisible)
                .collect(Collectors.toList());
    }

    public EducationEntity updateEducationItem(int id, EducationDto educationDto) {
        Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(id); //To Change
        List<EducationEntity> currentEducation = userProfile.get().getEducationHistory();
        EducationEntity education = educationRepository.findById(id).get();
        education.setLevelOfEducation(educationDto.getLevelOfEducation());
        education.setSchool(educationDto.getSchool());
        education.setStartDate(educationDto.getStartDate());
        education.setCurrentlyEnrolled(educationDto.isCurrentlyEnrolled());
        education.setEndDate(educationDto.getEndDate());
        education.setVisible(educationDto.isVisible());
        currentEducation.add(education);
        userProfile.get().setEducationHistory(currentEducation);
        log.info("UpdatedEducationEntry : Successfully updated a  UserProfile with Id : {}", userProfile.get().getId());
        educationRepository.save(education);
        return education;
    }

    public ResponseMessage toggleHideShowEducationEntry(int id) {
        EducationEntity education = educationRepository.getById(id);
        education.setVisible(!education.isVisible());
        educationRepository.save(education);
        log.info("HideShowEducationEntry : Successfully toggled education visibility from {} to {}", education.isVisible(), !education.isVisible());
        return new ResponseMessage("Education Entry Toggled to Hide/Show");
    }
}
