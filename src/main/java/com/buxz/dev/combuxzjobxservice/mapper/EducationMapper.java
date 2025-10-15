package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.EducationDto;
import com.buxz.dev.combuxzjobxservice.entity.EducationEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
