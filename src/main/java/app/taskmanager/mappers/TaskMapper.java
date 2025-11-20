package app.taskmanager.mappers;

import app.taskmanager.controller.dto.TaskDto;
import app.taskmanager.entities.Task;

public interface TaskMapper {

    Task fromDto(TaskDto taskDto);

    TaskDto toDto(Task task);
}
