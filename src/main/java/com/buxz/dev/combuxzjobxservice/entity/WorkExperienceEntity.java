package com.buxz.dev.combuxzjobxservice.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "work_experience")
public class WorkExperienceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String experienceJobTitle;
    private String employer;
    private String startDate;
    private String endDate;
    @Lob
    private String jobDescription;
    private boolean stillWorksHere;
    @ManyToOne(fetch= FetchType.EAGER, optional=true, cascade=CascadeType.ALL)
    @JoinColumn(name = "work_experience_id", nullable = true)
    private UserProfileEntity userProfile;
}
