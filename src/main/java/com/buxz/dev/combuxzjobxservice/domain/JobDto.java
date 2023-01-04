package com.buxz.dev.combuxzjobxservice.domain;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobDto {
    private String jobTitle;
    private String jobDescription;
    private String employer;
    private JobType jobType;
    private JobCurrentState jobState;
    private String salary;
    private String jobCity;
    private LocalDate closingDate;
    private boolean published;
}
