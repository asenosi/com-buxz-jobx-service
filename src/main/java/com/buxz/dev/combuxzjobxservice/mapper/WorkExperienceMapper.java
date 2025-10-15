package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.WorkExperienceDto;
import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkExperienceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfile", ignore = true)
    WorkExperienceEntity toEntity(WorkExperienceDto workExperienceDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "employer")
    @Mapping(target = "experienceJobTitle")
    @Mapping(target = "jobDescription")
    @Mapping(target = "startDate")
    @Mapping(target = "endDate")
    @Mapping(target = "stillWorksHere")
    void updateWorkExperienceFromDto(WorkExperienceDto workExperienceDto,
                                     @MappingTarget WorkExperienceEntity workExperienceEntity);
}
