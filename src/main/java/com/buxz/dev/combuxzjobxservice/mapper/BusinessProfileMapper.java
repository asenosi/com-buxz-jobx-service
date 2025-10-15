package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.BusinessProfileDto;
import com.buxz.dev.combuxzjobxservice.entity.BusinessProfileEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.ProfileStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BusinessProfileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imagesList", ignore = true)
    @Mapping(target = "profileStatus", expression = "java(ProfileStatus.ACTIVE)")
    @Mapping(target = "showProfile", constant = "true")
    BusinessProfileEntity toEntity(BusinessProfileDto businessProfileDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "businessName")
    @Mapping(target = "businessOwner")
    @Mapping(target = "businessCategory")
    @Mapping(target = "address")
    @Mapping(target = "contactDetails")
    @Mapping(target = "businessStartDate")
    @Mapping(target = "operationStartDate")
    @Mapping(target = "operationEndDate")
    @Mapping(target = "deliver")
    @Mapping(target = "profileStatus", expression = "java(ProfileStatus.ACTIVE)")
    void updateBusinessProfileFromDto(BusinessProfileDto businessProfileDto,
                                      @MappingTarget BusinessProfileEntity businessProfileEntity);
}
