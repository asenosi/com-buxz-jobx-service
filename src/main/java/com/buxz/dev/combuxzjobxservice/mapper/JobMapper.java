package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.JobDto;
import com.buxz.dev.combuxzjobxservice.entity.JobEntryEntity;
import com.buxz.dev.combuxzjobxservice.entity.embeddables.JobCurrentState;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface JobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
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
