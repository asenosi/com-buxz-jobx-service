package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.EducationDto;
import com.buxz.dev.combuxzjobxservice.entity.EducationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EducationMapper {

    @Mapping(target = "id", ignore = true)
    EducationEntity toEntity(EducationDto educationDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "levelOfEducation")
    @Mapping(target = "school")
    @Mapping(target = "currentlyEnrolled")
    @Mapping(target = "startDate")
    @Mapping(target = "endDate")
    @Mapping(target = "visible")
    void updateEducationFromDto(EducationDto educationDto, @MappingTarget EducationEntity educationEntity);
}
