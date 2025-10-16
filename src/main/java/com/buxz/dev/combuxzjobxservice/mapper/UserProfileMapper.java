package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.TestimonialDto;
import com.buxz.dev.combuxzjobxservice.domain.UserProfileDto;
import com.buxz.dev.combuxzjobxservice.domain.WorkExperienceDto;
import com.buxz.dev.combuxzjobxservice.entity.TestimonialEntity;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface UserProfileMapper {

    @Mapping(target = "educationHistory", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profileStatus", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    UserProfileEntity toEntity(UserProfileDto userProfileDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "jobTitle")
    @Mapping(target = "profileSummary")
    @Mapping(target = "dateOfBirth")
    @Mapping(target = "city")
    @Mapping(target = "showProfile")
    @Mapping(target = "contactDetails")
    void updateUserProfileFromDto(UserProfileDto userProfileDto, @MappingTarget UserProfileEntity userProfileEntity);

    @Mapping(target = "id", ignore = true)
    TestimonialEntity toTestimonialEntity(TestimonialDto testimonialDto);
    List<TestimonialEntity> toTestimonialEntitiesList(List<TestimonialDto> testimonialDtoList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    WorkExperienceEntity toWorkExperienceEntity(WorkExperienceDto workExperienceDto);
    List<WorkExperienceEntity> toWorkExperienceEntitiesLit(List<WorkExperienceDto> workExperienceDtos);
}
