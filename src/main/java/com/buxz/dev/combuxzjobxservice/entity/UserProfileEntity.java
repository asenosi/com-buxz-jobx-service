package com.buxz.dev.combuxzjobxservice.entity;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.ContactDetails;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UserProfileEntity {

    @Id
    private int id;
    private String jobTitle;
    @Lob
    private String profileSummary;
    private LocalDate dateOfBirth;
    private String city;
    boolean showProfile;
    private ProfileStatus profileStatus;
    @Embedded
    private ContactDetails contactDetails;
    @CreationTimestamp
    private LocalDateTime createdDate;
    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private List<TestimonialEntity> testimonialEntities;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private List<WorkExperienceEntity> workExperienceEntity;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private List<EducationEntity> educationHistory;
    //private EmployerReference reference;
    //TODO Create a relationship with UserAccount
    //    @OneToOne
    //    @JoinColumn()
    //    private UserAccount userAccount;
//    @OneToOne(mappedBy = "userProfile")
//    private UserAccountEntity userAccount;

}
