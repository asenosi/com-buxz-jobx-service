package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.WorkExperienceDto;
import com.buxz.dev.combuxzjobxservice.entity.WorkExperienceEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
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
