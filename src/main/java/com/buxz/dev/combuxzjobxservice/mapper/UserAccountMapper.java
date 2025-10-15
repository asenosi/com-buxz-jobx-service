package com.buxz.dev.combuxzjobxservice.mapper;

import com.buxz.dev.combuxzjobxservice.domain.UserAccountDto;
import com.buxz.dev.combuxzjobxservice.entity.UserAccountEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userProfiles", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.LocalDateTime.now())")
    UserAccountEntity toEntity(UserAccountDto userAccountDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "email")
    @Mapping(target = "firstName")
    @Mapping(target = "lastName")
    @Mapping(target = "cellNumber")
    void updateUserAccountFromDto(UserAccountDto userAccountDto, @MappingTarget UserAccountEntity userAccountEntity);
}
