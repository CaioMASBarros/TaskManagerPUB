package app.taskmanager.controller;

import app.taskmanager.controller.dto.TaskDto;
import app.taskmanager.entities.Task;
import app.taskmanager.mappers.TaskMapper;
import app.taskmanager.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/task-lists/{task_list_id}/tasks")
@EnableMethodSecurity
public class TasksController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TasksController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<List<TaskDto>> listTasks(@PathVariable("task_list_id") UUID taskListId) {
        List<TaskDto> tasks = taskService.listTasks(taskListId)
                .stream()
                .map(taskMapper::toDto)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<TaskDto> createTask(
            @PathVariable("task_list_id") UUID taskListId,
            @RequestBody TaskDto taskDto
    ) {
        Task createdTask = taskService.createTask(taskListId, taskMapper.fromDto(taskDto));
        TaskDto response = taskMapper.toDto(createdTask);

        URI location = URI.create(String.format(
                "/task-lists/%s/tasks/%s",
                taskListId, createdTask.getId()
        ));

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{task_id}")
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<TaskDto> getTask(
            @PathVariable("task_list_id") UUID taskListId,
            @PathVariable("task_id") UUID taskId
    ) {
        return taskService.getTask(taskListId, taskId)
                .map(taskMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{task_id}")
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable("task_list_id") UUID taskListId,
            @PathVariable("task_id") UUID taskId,
            @RequestBody TaskDto taskDto
    ) {
        Task updated = taskService.UpdateTask(taskListId, taskId, taskMapper.fromDto(taskDto));
        return ResponseEntity.ok(taskMapper.toDto(updated));
    }

    @DeleteMapping("/{task_id}")
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("task_list_id") UUID taskListId,
            @PathVariable("task_id") UUID taskId
    ) {
        taskService.deleteTask(taskListId, taskId);
        return ResponseEntity.noContent().build();
    }
}
