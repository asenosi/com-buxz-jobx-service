package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.BusinessProfileDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.BusinessProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.BusinessCategory;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.SupportingImages;
import com.buxz.dev.combuxzjobxservice.repository.BusinessProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;
    private final ImageRepository imageRepository;
    private final UploadService uploadService;

    @Autowired
    public BusinessProfileService(BusinessProfileRepository businessProfileRepository, ImageRepository imageRepository, UploadService uploadService) {
        this.businessProfileRepository = businessProfileRepository;
        this.imageRepository = imageRepository;
        this.uploadService = uploadService;
    }

    public BusinessProfileEntity createBusinessProfile(BusinessProfileDto businessProfileDto) {
        BusinessProfileEntity businessProfile = BusinessProfileEntity.builder()
                .businessName(businessProfileDto.getBusinessName())
                .businessOwner(businessProfileDto.getBusinessOwner())
                .businessCategory(businessProfileDto.getBusinessCategory())
                .address((businessProfileDto.getAddress()))
                .contactDetails(businessProfileDto.getContactDetails())
                .businessStartDate(businessProfileDto.getBusinessStartDate())
                .operationStartDate(businessProfileDto.getOperationStartDate())
                .operationEndDate(businessProfileDto.getOperationEndDate())
                .profileStatus(ProfileStatus.ACTIVE)
                .showProfile(true)
                .deliver((businessProfileDto.isDeliver()))
                .build();
        businessProfileRepository.save(businessProfile);
        log.info("CreateBusinessProfile : Successfully created a new Business Profile for Username : TOADD");
        return businessProfile;
    }

    public List<BusinessProfileEntity> getListOfAllBusinessProfiles() {
        return businessProfileRepository.findAll();
    }

    public List<BusinessProfileEntity> getListOfBusinessProfilesContaining(String businessProfile) {
        return businessProfileRepository.findAll()
                .parallelStream()
                .filter(profile -> profile.getBusinessName().contains(businessProfile))
                .collect(Collectors.toList());
    }

    public List<BusinessProfileEntity> getBusinessProfilesByCategory(BusinessCategory businessCategory) {
        return businessProfileRepository.findAll()
                .parallelStream()
                .filter(p -> p.getBusinessCategory().equals(businessCategory))
                .collect(Collectors.toList());
    }

    public Optional<BusinessProfileEntity> getBusinessProfileById(int profileId) {
        return businessProfileRepository.findById(profileId);
    }

    public BusinessProfileEntity updateBusinessProfile(int profileId, BusinessProfileDto businessProfileDto) {
        Optional<BusinessProfileEntity> businessProfileToUpdate = getBusinessProfileById(profileId);
        if(businessProfileToUpdate.isPresent()) {
            businessProfileToUpdate.get().setBusinessName(businessProfileDto.getBusinessName());
            businessProfileToUpdate.get().setBusinessOwner(businessProfileDto.getBusinessOwner());
            businessProfileToUpdate.get().setBusinessCategory(businessProfileDto.getBusinessCategory());
            businessProfileToUpdate.get().setAddress(businessProfileDto.getAddress());
            businessProfileToUpdate.get().setContactDetails(businessProfileDto.getContactDetails());
            businessProfileToUpdate.get().setBusinessStartDate(businessProfileDto.getBusinessStartDate());
            businessProfileToUpdate.get().setOperationEndDate(businessProfileDto.getOperationStartDate());
            businessProfileToUpdate.get().setOperationEndDate(businessProfileDto.getOperationEndDate());
            businessProfileToUpdate.get().setProfileStatus(ProfileStatus.ACTIVE);
            businessProfileToUpdate.get().setDeliver(businessProfileDto.isDeliver());
            businessProfileRepository.save(businessProfileToUpdate.get());
            log.info("UpdateBusinessProfile : Successfully updated a Business Profile with ID {} :", profileId);

        } else {
            log.info("UpdateBusinessProfile : Failed to update a cause UserProfile with Id : {} NOT_FOUND", profileId);
        }
        return businessProfileToUpdate.get();
    }

    public SupportingImages addSupportingImages(int bid, MultipartFile file) {
        Optional<BusinessProfileEntity> businessProfile = businessProfileRepository.findById(bid);
        SupportingImages image = new SupportingImages();
        if (businessProfile.isPresent()) {
            try {
                uploadService.uploadFile(file);
                List<SupportingImages> currentImageList = businessProfile.get().getImagesList();
                image.setImageFileName(file.getOriginalFilename());
                image.setImageUrl("/jobxstore/" + file.getOriginalFilename());
                image.setUploadedDate(LocalDateTime.now().toString());
                image.setTag("ImageTag");
                imageRepository.saveAndFlush(image);
                currentImageList.add(image);
                businessProfile.get().setImagesList(currentImageList);
            } catch (Exception e) {
                log.error("Failed to upload file due to {}", e.getMessage());
            }
            businessProfileRepository.save(businessProfile.get());
            log.info("AddSupportingImage : Added support image to business_profile_id {}", bid);
        } else {
            log.warn("AddSupportingImage : Failed to add support image, because profile_id {} not found", bid);
        }
        return image;
    }

    public ResponseMessage deactivateBusinessProfile(int profileId) {
        log.info("Received a request to Deactivate a Business ProfileId: {}", profileId);
        Optional<BusinessProfileEntity> profileToDeactivate = getBusinessProfileById(profileId);
        if (profileToDeactivate.isPresent()) {
            profileToDeactivate.get().setShowProfile(false);
            profileToDeactivate.get().setProfileStatus(ProfileStatus.DEACTIVATED);
            businessProfileRepository.save(profileToDeactivate.get());
            log.info("DeactivateBusinessProfile: Business Profile with Id {} : Deactivated successfully ", profileId);
            return new ResponseMessage("DeactivateBusinessProfile: Business Profile with Id " + profileId + " - Deactivated successfully ");
        } else {
            log.warn("DeactivateBusinessProfile: Profile Already Deactivated or Suspended");
            return new ResponseMessage("DeactivateBusinessProfile: Profile Already Deactivated or Suspended");
        }
    }
}
