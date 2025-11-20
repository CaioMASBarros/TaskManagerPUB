package app.taskmanager.controller;

import app.taskmanager.controller.dto.TaskListDto;
import app.taskmanager.entities.TaskList;
import app.taskmanager.mappers.TaskListMapper;
import app.taskmanager.repositories.UserRepository;
import app.taskmanager.service.TaskListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/task-lists")
@EnableMethodSecurity
public class TaskListController {

    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;
    private final UserRepository userRepository;

    public TaskListController(
            TaskListService taskListService,
            TaskListMapper taskListMapper,
            UserRepository userRepository
    ) {
        this.taskListService = taskListService;
        this.taskListMapper = taskListMapper;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_basic') or hasAuthority('SCOPE_admin')")
    public ResponseEntity<List<TaskListDto>> listTaskLists() {
        List<TaskListDto> lists = taskListService.listTaskLists()
                .stream()
                .map(taskListMapper::toDto)
                .toList();

        return ResponseEntity.ok(lists);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<TaskListDto> createTaskList(
            @RequestBody TaskListDto taskListDto,
            JwtAuthenticationToken token
    ) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new RuntimeException("User não encontrado"));

        TaskList created = taskListService.createTasklist(
                taskListMapper.fromDto(taskListDto),
                user
        );

        TaskListDto response = taskListMapper.toDto(created);

        URI location = URI.create("/task-lists/" + created.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{task_list_id}")
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<TaskListDto> getTaskList(
            @PathVariable("task_list_id") UUID taskListId,
            JwtAuthenticationToken token
    ) {
        return taskListService.getTaskList(taskListId)
                .map(taskListMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{task_list_id}")
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<TaskListDto> updateTaskList(
            @PathVariable("task_list_id") UUID taskListId,
            @RequestBody TaskListDto taskListDto
    ) {
        TaskList updated = taskListService.updateTaskList(
                taskListId,
                taskListMapper.fromDto(taskListDto)
        );

        return ResponseEntity.ok(taskListMapper.toDto(updated));
    }

    @DeleteMapping("/{task_list_id}")
    @PreAuthorize("hasAuthority('SCOPE_basic')")
    public ResponseEntity<Void> deleteTaskList(
            @PathVariable("task_list_id") UUID taskListId
    ) {
        taskListService.deleteTaskList(taskListId);
        return ResponseEntity.noContent().build();
    }
}
