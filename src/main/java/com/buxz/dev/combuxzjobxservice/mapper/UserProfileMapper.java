package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.UserProfileDto;
import com.buxz.dev.combuxzjobxservice.entity.UserProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {

    @Mapping(target = "testimonialEntity", ignore = true)
    @Mapping(target = "workExperienceEntity", ignore = true)
    @Mapping(target = "educationHistory", ignore = true)
    @Mapping(target = "profileStatus", expression = "java(ProfileStatus.ACTIVE)")
    UserProfileEntity toEntity(UserProfileDto userProfileDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "jobTitle")
    @Mapping(target = "profileSummary")
    @Mapping(target = "dateOfBirth")
    @Mapping(target = "city")
    @Mapping(target = "showProfile")
    @Mapping(target = "contactDetails")
    void updateUserProfileFromDto(UserProfileDto userProfileDto, @MappingTarget UserProfileEntity userProfileEntity);
}
