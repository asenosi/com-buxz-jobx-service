package com.buxz.dev.combuxzjobxservice.domain;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.ContactDetails;
import com.buxz.dev.combuxzjobxservice.entity.TestimonialEntity;
import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserProfileDto {
    private String jobTitle;
    private String profileSummary;
    private LocalDate dateOfBirth;
    private String city;
    boolean showProfile = true;
    private ContactDetails contactDetails;
    private List<TestimonialEntity> testimonialEntities;
    private List<WorkExperienceEntity> workExperienceEntity;
}
