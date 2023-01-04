package com.buxz.dev.combuxzjobxservice.domain;

import com.buxz.dev.combuxzjobxservice.entity.embeddables.BusinessAddress;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.BusinessCategory;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ContactDetails;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import lombok.Data;

import javax.persistence.Embedded;

@Data
public class BusinessProfileDto {
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
}
