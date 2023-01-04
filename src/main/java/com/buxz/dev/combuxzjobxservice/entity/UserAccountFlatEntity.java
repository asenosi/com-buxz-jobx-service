package com.buxz.dev.combuxzjobxservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountFlatEntity {

    int id;
    private String email;
    private String firstName;
    private String lastName;
    private String cellNumber;
    private String userName;
    private String dateCreated;
    private String jobTitle;
    private String profileSummary;
    private String dateOfBirth;
    private String city;
    private List<EducationEntity> educationHistory;
    private List<TestimonialEntity> testimonials;
    private List<WorkExperienceEntity> workExperiences;
//    private String showProfile;
//    private String profileStatus;
//    private String whatsappNumber;
//    private String linkedInLink;
//    private String facebookLink;
//    private String createdDate;
//    private String updatedDate;
    //testimonialEntity: [ ],
    //workExperienceEntity: [ ],
    //educationHistory: [ ]

}
