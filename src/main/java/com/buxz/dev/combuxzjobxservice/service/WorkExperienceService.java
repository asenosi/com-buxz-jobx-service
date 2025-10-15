package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.domain.WorkExperienceDto;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import com.buxz.dev.combuxzjobxservice.repository.UserProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.WorkExperienceRepository;
import com.buxz.dev.combuxzjobxservice.mapper.WorkExperienceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class WorkExperienceService {

    private final WorkExperienceRepository workRepository;
    private final UserProfileRepository userProfileRepository;
    private final WorkExperienceMapper workExperienceMapper;

    @Autowired
    public WorkExperienceService(WorkExperienceRepository workRepository, UserProfileRepository userProfileRepository,
                                 WorkExperienceMapper workExperienceMapper) {
        this.workRepository = workRepository;
        this.userProfileRepository = userProfileRepository;
        this.workExperienceMapper = workExperienceMapper;
    }

    public WorkExperienceEntity addWorkExperience(int id, WorkExperienceDto workDto) {
        Optional<UserProfileEntity> userProfile = userProfileRepository.findById(id);
        WorkExperienceEntity workExperience = workExperienceMapper.toEntity(workDto);
        if (userProfile.isPresent()) {
            List<WorkExperienceEntity> currentWorkList= userProfile.get().getWorkExperienceEntity();
            currentWorkList.add(workExperience);
            workRepository.saveAndFlush(workExperience);
            userProfile.get().setWorkExperienceEntity(currentWorkList);
            userProfileRepository.save(userProfile.get());
            log.info("AddWorkExperience : Added work experience to profile_id {}", id);
        } else {
            log.warn("AddWorkExperience : Failed to add work experience, because profile_id {} not found", id);
        }
        return workExperience;
    }

    public Optional<WorkExperienceEntity> getWorkExperienceById(int workId) {
        return workRepository.findById(workId);
    }

    public WorkExperienceEntity updateWorkExperience(int userId, int workId, WorkExperienceDto workExperienceDto) {
        Optional<WorkExperienceEntity> workExperience = workRepository.findById(workId);
        if (workExperience.isPresent()) {
            workExperienceMapper.updateWorkExperienceFromDto(workExperienceDto, workExperience.get());
            workRepository.save(workExperience.get());
            log.info("UpdateWorkExperience : Added work experience to profile_id {}", userId);
        } else {
            log.warn("UpdateWorkExperience : Failed to update work experience to profile_id {}, no work experience with id {} found", userId, workId);
        }
        return workExperience.get();
    }

    public ResponseMessage deleteWorkExperience(int userId, int workId) {
        workRepository.deleteById(workId);
        return new ResponseMessage("DeleteWorkExperience : Work experience deleted successfully");
    }
}
