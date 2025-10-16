package com.buxz.dev.combuxzjobxservice.service;

import com.buxz.dev.combuxzjobxservice.domain.BusinessProfileDto;
import com.buxz.dev.combuxzjobxservice.domain.ResponseMessage;
import com.buxz.dev.combuxzjobxservice.entity.BusinessProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.BusinessCategory;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.SupportingImages;
import com.buxz.dev.combuxzjobxservice.exception.NotFoundExeption;
import com.buxz.dev.combuxzjobxservice.repository.BusinessProfileRepository;
import com.buxz.dev.combuxzjobxservice.repository.ImageRepository;
import com.buxz.dev.combuxzjobxservice.mapper.BusinessProfileMapper;
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
    private final BusinessProfileMapper businessProfileMapper;

    @Autowired
    public BusinessProfileService(BusinessProfileRepository businessProfileRepository, ImageRepository imageRepository,
                                 UploadService uploadService, BusinessProfileMapper businessProfileMapper) {
        this.businessProfileRepository = businessProfileRepository;
        this.imageRepository = imageRepository;
        this.uploadService = uploadService;
        this.businessProfileMapper = businessProfileMapper;
    }

    public BusinessProfileEntity createBusinessProfile(BusinessProfileDto businessProfileDto) {
        BusinessProfileEntity businessProfile = businessProfileMapper.toEntity(businessProfileDto);
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
            businessProfileMapper.updateBusinessProfileFromDto(businessProfileDto, businessProfileToUpdate.get());
            businessProfileRepository.save(businessProfileToUpdate.get());
            log.info("UpdateBusinessProfile : Successfully updated a Business Profile with ID {} :", profileId);
        } else {
            log.info("UpdateBusinessProfile : Failed to update a cause UserProfile with Id : {} NOT_FOUND", profileId);
            throw new NotFoundExeption("Business Profile not found");
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
