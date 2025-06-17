package com.demo.copilot.taskmanager.application.mapper;

import com.demo.copilot.taskmanager.application.dto.user.UserResponse;
import com.demo.copilot.taskmanager.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for User entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "email.value", target = "email")
    @Mapping(expression = "java(user.getFullName())", target = "fullName")
    UserResponse toResponse(User user);
}