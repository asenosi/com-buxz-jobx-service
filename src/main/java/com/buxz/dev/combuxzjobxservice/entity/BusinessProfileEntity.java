package com.buxz.dev.combuxzjobxservice.entity;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "business_profile")
public class BusinessProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String businessName;
    private String businessOwner;
    @Embedded
    private BusinessAddress address;
    @Embedded
    private ContactDetails contactDetails;
    private String businessStartDate;
    private String operationStartDate;
    private String operationEndDate;
    private boolean deliver;
    private ProfileStatus profileStatus;
    private BusinessCategory businessCategory;
    private boolean showProfile;
    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "business_profile_id", referencedColumnName = "id")
    private List<SupportingImages> imagesList;
    //private Services services;
}
