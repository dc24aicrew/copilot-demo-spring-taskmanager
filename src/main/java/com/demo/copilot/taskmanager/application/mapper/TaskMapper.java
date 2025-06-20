package com.demo.copilot.taskmanager.application.mapper;

import com.demo.copilot.taskmanager.application.dto.task.TaskResponse;
import com.demo.copilot.taskmanager.application.dto.task.TaskSummaryResponse;
import com.demo.copilot.taskmanager.domain.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Task entity and DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "assignedTo.value", target = "assignedTo")
    @Mapping(source = "createdBy.value", target = "createdBy")
    TaskResponse toResponse(Task task);

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "assignedTo.value", target = "assignedTo")
    @Mapping(source = "createdBy.value", target = "createdBy")
    TaskSummaryResponse toSummaryResponse(Task task);
}