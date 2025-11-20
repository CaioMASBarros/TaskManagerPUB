package app.taskmanager.controller;

import app.taskmanager.controller.dto.TaskDto;
import app.taskmanager.entities.Task;
import app.taskmanager.mappers.TaskMapper;
import app.taskmanager.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TasksController tasksController;

    @Nested
    class listTasks{
        @Test
        @DisplayName("Deve listar tarefas com sucesso")
        void shouldListTasksSuccessfully() {


            UUID listId = UUID.randomUUID();

            Task t1 = new Task();
            t1.setId(UUID.randomUUID());
            t1.setTitle("A");
            t1.setDescription("desc");

            Task t2 = new Task();
            t2.setId(UUID.randomUUID());
            t2.setTitle("B");
            t2.setDescription("desc");


            TaskDto dto1 = new TaskDto(UUID.randomUUID(), "A", "desc", null, null, null);
            TaskDto dto2 = new TaskDto(UUID.randomUUID(), "B", "desc", null, null, null);

            when(taskService.listTasks(listId)).thenReturn(List.of(t1, t2));
            when(taskMapper.toDto(t1)).thenReturn(dto1);
            when(taskMapper.toDto(t2)).thenReturn(dto2);

            ResponseEntity<List<TaskDto>> response = tasksController.listTasks(listId);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(List.of(dto1, dto2), response.getBody());
            verify(taskService).listTasks(listId);
        }
    }






    @Test
    @DisplayName("Deve criar uma tarefa com sucesso")
    void shouldCreateTaskSuccessfully() {
        UUID listId = UUID.randomUUID();

        TaskDto requestDto = new TaskDto(null, "New", "desc", LocalDateTime.now(), null, null);
        Task createdEntity = new Task();
        createdEntity.setId(UUID.randomUUID());

        TaskDto responseDto = new TaskDto(createdEntity.getId(), "New", "desc", null, null, null);

        when(taskMapper.fromDto(requestDto)).thenReturn(createdEntity);
        when(taskService.createTask(listId, createdEntity)).thenReturn(createdEntity);
        when(taskMapper.toDto(createdEntity)).thenReturn(responseDto);

        ResponseEntity<TaskDto> response = tasksController.createTask(listId, requestDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
        assertEquals(URI.create(
                "/task-lists/" + listId + "/tasks/" + createdEntity.getId()
        ), response.getHeaders().getLocation());

        verify(taskService).createTask(listId, createdEntity);
    }


    @Test
    @DisplayName("Deve retornar uma tarefa existente")
    void shouldReturnTaskIfExists() {
        UUID listId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        Task task = new Task();
        TaskDto dto = new TaskDto(taskId, "Teste", "desc", null, null, null);

        when(taskService.getTask(listId, taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(dto);

        ResponseEntity<TaskDto> response = tasksController.getTask(listId, taskId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    @DisplayName("Deve retornar 404 quando tarefa não existir")
    void shouldReturn404IfTaskNotFound() {
        UUID listId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        when(taskService.getTask(listId, taskId)).thenReturn(Optional.empty());

        ResponseEntity<TaskDto> response = tasksController.getTask(listId, taskId);

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }


    @Test
    @DisplayName("Deve atualizar uma tarefa com sucesso")
    void shouldUpdateTaskSuccessfully() {
        UUID listId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        TaskDto request = new TaskDto(taskId, "Updated", "desc", null, null, null);
        Task updatedEntity = new Task();
        updatedEntity.setId(taskId);

        TaskDto responseDto = new TaskDto(taskId, "Updated", "desc", null, null, null);

        when(taskMapper.fromDto(request)).thenReturn(updatedEntity);
        when(taskService.UpdateTask(listId, taskId, updatedEntity)).thenReturn(updatedEntity);
        when(taskMapper.toDto(updatedEntity)).thenReturn(responseDto);

        ResponseEntity<TaskDto> response = tasksController.updateTask(listId, taskId, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
    }


    @Test
    @DisplayName("Deve deletar tarefa com sucesso")
    void shouldDeleteTaskSuccessfully() {
        UUID listId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();

        ResponseEntity<Void> response = tasksController.deleteTask(listId, taskId);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(taskService).deleteTask(listId, taskId);
    }
}
