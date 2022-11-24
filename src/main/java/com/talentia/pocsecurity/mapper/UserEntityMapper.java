package com.talentia.pocsecurity.mapper;

import com.talentia.pocsecurity.domain.UserEntity;
import com.talentia.pocsecurity.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "notLocked", target = "isNotLocked")
    UserDto entityToDto(UserEntity user);
    @Mapping(source = "active", target = "isActive")
    @Mapping(source = "notLocked", target = "isNotLocked")
    UserEntity dtoToEntity(UserDto user);
}
