package app.taskmanager.controller.dto;

import app.taskmanager.entities.TaskPriority;
import app.taskmanager.entities.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDto(UUID id, String title, String description, LocalDateTime dueDate, TaskPriority priority, TaskStatus status) {
}
