package com.buxz.dev.combuxzjobxservice.entity;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.LevelOfEducation;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "education_history")
public class EducationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated
    private LevelOfEducation levelOfEducation;
    private String school;
    private boolean currentlyEnrolled;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean visible;
//    @ManyToOne(fetch=FetchType.EAGER, optional=true, cascade=CascadeType.ALL)
//    @JoinColumn(name = "education_history_id", nullable = true)
//    private UserProfileEntity userProfile;
}
