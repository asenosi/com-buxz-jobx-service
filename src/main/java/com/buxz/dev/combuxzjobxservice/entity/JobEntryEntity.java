package com.buxz.dev.combuxzjobxservice.entity;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Entity
public class JobEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String jobTitle;
    private String jobDescription;
    private String employer;
    @Enumerated(EnumType.STRING)
    private JobType jobType;
    private String salary;
    private String jobCity;
    private LocalDate closingDate;

    @Enumerated(EnumType.STRING)
    private JobCurrentState jobState;
    private boolean published = false;
    private LocalDateTime dateCreated;

    public JobEntryEntity() {
    }
}
