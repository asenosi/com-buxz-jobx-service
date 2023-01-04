package com.buxz.dev.combuxzjobxservice.domain;

import lombok.Data;

import javax.persistence.Lob;

@Data
public class WorkExperienceDto {
    private String experienceJobTitle;
    private String employer;
    private String startDate;
    private String endDate;
    @Lob
    private String jobDescription;
    private boolean stillWorksHere;
}
