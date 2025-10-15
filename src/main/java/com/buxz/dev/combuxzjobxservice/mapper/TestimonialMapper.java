package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.TestimonialDto;
import com.buxz.dev.combuxzjobxservice.entity.TestimonialEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TestimonialMapper {

    @Mapping(target = "id", ignore = true)
    TestimonialEntity toEntity(TestimonialDto testimonialDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "testimonialBy")
    @Mapping(target = "testimonialSummary")
    @Mapping(target = "testimonialDescription")
    @Mapping(target = "fileName")
    void updateTestimonialFromDto(TestimonialDto testimonialDto, @MappingTarget TestimonialEntity testimonialEntity);
}
