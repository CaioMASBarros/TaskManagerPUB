package app.taskmanager.controller;

import app.taskmanager.controller.dto.TaskListDto;
import app.taskmanager.entities.TaskList;
import app.taskmanager.entities.User;
import app.taskmanager.mappers.TaskListMapper;
import app.taskmanager.repositories.UserRepository;
import app.taskmanager.service.TaskListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskListControllerTest {

    @Mock
    private TaskListService taskListService;

    @Mock
    private TaskListMapper taskListMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskListController taskListController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Nested
    class listTaskListsTests {

        @Test
        @DisplayName("Deve retornar listas com sucesso")
        void shouldReturnTaskListsWithSuccess() {
            TaskList entity1 = new TaskList();
            TaskList entity2 = new TaskList();

            TaskListDto dto1 = new TaskListDto(
                    UUID.randomUUID(), "A", "desc", 0, 0.0, List.of()
            );
            TaskListDto dto2 = new TaskListDto(
                    UUID.randomUUID(), "B", "desc", 0, 0.0, List.of()
            );

            List<TaskList> mockList = List.of(entity1, entity2);

            when(taskListService.listTaskLists()).thenReturn(mockList);

            when(taskListMapper.toDto(any(TaskList.class)))
                    .thenReturn(dto1)
                    .thenReturn(dto2);

            ResponseEntity<List<TaskListDto>> response = taskListController.listTaskLists();

            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());

            assertEquals(dto1, response.getBody().get(0));
            assertEquals(dto2, response.getBody().get(1));

            verify(taskListService).listTaskLists();
            verify(taskListMapper, times(2)).toDto(any(TaskList.class));
        }



        @Test
        @DisplayName("Deve lançar exceção quando erro ocorrer")
        void shouldThrowExceptionWhenErrorOccurs() {
            when(taskListService.listTaskLists()).thenThrow(new RuntimeException("Erro"));

            assertThrows(RuntimeException.class, () -> taskListController.listTaskLists());
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void shouldReturnEmptyList() {
            when(taskListService.listTaskLists()).thenReturn(List.of());

            ResponseEntity<List<TaskListDto>> response = taskListController.listTaskLists();

            assertTrue(response.getBody().isEmpty());
            assertEquals(200, response.getStatusCodeValue());
        }
    }


    @Nested
    class createTaskListTests {

        @Test
        @DisplayName("Deve criar TaskList com sucesso")
        void shouldCreateTaskListWithSuccess() {
            UUID userId = UUID.randomUUID();
            UUID createdId = UUID.randomUUID();

            TaskListDto inputDto = new TaskListDto(
                    null, "Título", "Descrição", 0, 0.0, List.of()
            );

            TaskList entity = new TaskList();
            entity.setId(createdId);

            TaskListDto outputDto = new TaskListDto(
                    createdId, "Título", "Descrição", 0, 0.0, List.of()
            );

            User user = new User();
            user.setUserId(userId);

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);

            when(token.getName()).thenReturn(userId.toString());
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(taskListMapper.fromDto(inputDto)).thenReturn(entity);
            when(taskListService.createTasklist(entity, user)).thenReturn(entity);
            when(taskListMapper.toDto(entity)).thenReturn(outputDto);

            ResponseEntity<TaskListDto> response =
                    taskListController.createTaskList(inputDto, token);

            assertEquals(201, response.getStatusCodeValue());
            assertEquals("/task-lists/" + createdId, response.getHeaders().getLocation().toString());
            assertEquals(outputDto, response.getBody());
            verify(taskListService).createTasklist(entity, user);
        }

        @Test
        @DisplayName("Deve lançar erro quando usuário não for encontrado")
        void shouldThrowErrorWhenUserNotFound() {
            UUID userId = UUID.randomUUID();
            TaskListDto dto = new TaskListDto(
                    null, "Título", "Descrição", 0, 0.0, List.of()
            );

            JwtAuthenticationToken token = mock(JwtAuthenticationToken.class);
            when(token.getName()).thenReturn(userId.toString());
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> taskListController.createTaskList(dto, token));
        }
    }


    @Nested
    class getTaskListTests {

        @Test
        @DisplayName("Deve retornar TaskList quando existir")
        void shouldReturnTaskListWhenExists() {
            UUID id = UUID.randomUUID();

            TaskList entity = new TaskList();
            TaskListDto dto = new TaskListDto(
                    id, "Titulo", "desc", 1, 0.5, List.of()
            );

            when(taskListService.getTaskList(id)).thenReturn(Optional.of(entity));
            when(taskListMapper.toDto(entity)).thenReturn(dto);

            ResponseEntity<TaskListDto> response = taskListController.getTaskList(id, null);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(dto, response.getBody());
        }

        @Test
        @DisplayName("Deve retornar 404 quando tasklist não existir")
        void shouldReturnNotFound() {
            UUID id = UUID.randomUUID();

            when(taskListService.getTaskList(id)).thenReturn(Optional.empty());

            ResponseEntity<TaskListDto> response = taskListController.getTaskList(id, null);

            assertEquals(404, response.getStatusCodeValue());
            assertNull(response.getBody());
        }
    }


    @Nested
    class updateTaskListTests {

        @Test
        @DisplayName("Deve atualizar TaskList com sucesso")
        void shouldUpdateTaskListWithSuccess() {
            UUID id = UUID.randomUUID();

            TaskListDto inputDto = new TaskListDto(
                    null, "Novo título", "Desc", 0, 0.0, List.of()
            );
            TaskList entity = new TaskList();
            TaskListDto outputDto = new TaskListDto(
                    id, "Novo título", "Desc", 0, 0.0, List.of()
            );

            when(taskListMapper.fromDto(inputDto)).thenReturn(entity);
            when(taskListService.updateTaskList(id, entity)).thenReturn(entity);
            when(taskListMapper.toDto(entity)).thenReturn(outputDto);

            ResponseEntity<TaskListDto> response = taskListController.updateTaskList(id, inputDto);

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(outputDto, response.getBody());
            verify(taskListService).updateTaskList(id, entity);
        }
    }

    @Nested
    class deleteTaskListTests {

        @Test
        @DisplayName("Deve deletar TaskList com sucesso")
        void shouldDeleteTaskListWithSuccess() {
            UUID id = UUID.randomUUID();

            ResponseEntity<Void> response = taskListController.deleteTaskList(id);

            assertEquals(204, response.getStatusCodeValue());
            verify(taskListService).deleteTaskList(id);
        }
    }
}
