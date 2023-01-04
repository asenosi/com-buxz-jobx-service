package com.buxz.dev.combuxzjobxservice.domain;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.LevelOfEducation;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationDto {
    private LevelOfEducation levelOfEducation;
    private String school;
    private boolean currentlyEnrolled;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean visible;
}
