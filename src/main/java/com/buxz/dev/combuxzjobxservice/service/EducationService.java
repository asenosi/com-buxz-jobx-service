package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.EducationDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.EducationEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.repository.EducationRepository;
import com.buxz.dev.combuxzjobxservice.mapper.EducationMapper;
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
    private final EducationMapper educationMapper;

    @Autowired
    public EducationService(final EducationRepository educationRepository,
                            final UserProfileService userProfileService,
                            final EducationMapper educationMapper) {
        this.educationRepository = educationRepository;
        this.userProfileService = userProfileService;
        this.educationMapper = educationMapper;
    }

    public EducationEntity addEducationEntry(int id, EducationDto educationDto) {
        EducationEntity educationEntry = educationMapper.toEntity(educationDto);
        Optional<UserProfileEntity> userProfile = userProfileService.getUserProfileById(id); //To Change
        List<EducationEntity> currentEducationHistory = userProfile.get().getEducationHistory();
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
        educationMapper.updateEducationFromDto(educationDto, education);
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
