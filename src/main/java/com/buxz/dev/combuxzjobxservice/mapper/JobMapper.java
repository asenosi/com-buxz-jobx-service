package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.JobDto;
import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobState", expression = "java(JobCurrentState.CREATED)")
    @Mapping(target = "published", constant = "false")
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    JobEntryEntity toEntity(JobDto jobDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "jobTitle")
    @Mapping(target = "jobDescription")
    @Mapping(target = "jobCity")
    @Mapping(target = "employer")
    @Mapping(target = "salary")
    @Mapping(target = "jobType")
    @Mapping(target = "closingDate")
    void updateJobEntryFromDto(JobDto jobDto, @MappingTarget JobEntryEntity jobEntryEntity);
}
