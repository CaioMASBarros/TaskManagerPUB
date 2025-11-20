package app.taskmanager.mappers;

import app.taskmanager.controller.dto.TaskDto;
import app.taskmanager.controller.dto.TaskListDto;
import app.taskmanager.entities.TaskList;

public interface TaskListMapper {

    TaskList fromDto(TaskListDto taskListDto);
    TaskListDto toDto(TaskList taskList);
}
