package app.taskmanager.service.impl;

import app.taskmanager.entities.Task;
import app.taskmanager.entities.TaskList;
import app.taskmanager.entities.TaskPriority;
import app.taskmanager.entities.TaskStatus;
import app.taskmanager.repositories.TaskListRepository;
import app.taskmanager.repositories.TaskRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    @Nested
    class listTasksTests {

        @Test
        @DisplayName("Deve retornar lista de Tasks")
        void shouldReturnListOfTasks() {

            UUID listId = UUID.randomUUID();
            List<Task> tasks = List.of(new Task(), new Task());

            when(taskRepository.findByTaskListId(listId)).thenReturn(tasks);

            List<Task> result = taskServiceImpl.listTasks(listId);

            assertEquals(2, result.size());
            verify(taskRepository, times(1)).findByTaskListId(listId);
        }

        @Test
        @DisplayName("Deve retornar exceção quando erro ocorrer")
        void shouldThrowExceptionWhenErrorOccurs(){
            UUID idTasklist = UUID.randomUUID();
            when(taskRepository.findByTaskListId(idTasklist)).thenThrow(new RuntimeException("Erro ocorreu"));

            assertThrows(RuntimeException.class, () -> taskServiceImpl.listTasks(idTasklist));
        }
    }



    @Nested
    class createTaskTests {

        @Test
        @DisplayName("Deve criar Task com sucesso")
        void shouldCreateTaskwithSuccess() {
            UUID listId = UUID.randomUUID();
            TaskList taskList = new TaskList();
            taskList.setId(listId);

            Task task = new Task();
            task.setTitle("My Task");

            when(taskListRepository.findById(listId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

            Task result = taskServiceImpl.createTask(listId, task);

            assertNotNull(result);
            assertEquals("My Task", result.getTitle());
            assertEquals(TaskStatus.OPEN, result.getStatus());
            assertEquals(TaskPriority.MEDIUM, result.getPriority());
            assertEquals(taskList, result.getTaskList());

            verify(taskRepository, times(1)).save(any(Task.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando task tem id")
        void shouldThrowWhenTaskHasId() {
            Task task = new Task();
            task.setId(UUID.randomUUID());

            UUID listId = UUID.randomUUID();

            assertThrows(IllegalArgumentException.class, () -> {
                taskServiceImpl.createTask(listId, task);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção quando titulo está faltando")
        void shouldThrowWhenTitleIsMissing() {
            Task task = new Task();
            task.setTitle(" ");

            UUID listId = UUID.randomUUID();

            assertThrows(IllegalArgumentException.class, () -> {
                taskServiceImpl.createTask(listId, task);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção quando TaskList não existe")
        void shouldThrowWhenTaskListDoesNotExist() {
            UUID listId = UUID.randomUUID();
            Task task = new Task();
            task.setTitle("Valid Title");

            when(taskListRepository.findById(listId)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                taskServiceImpl.createTask(listId, task);
            });
        }
    }


    @Nested
    class getTaskTests {

        @Test
        @DisplayName("Deve retornar Task com sucesso")
        void shouldReturnTaskSuccessfully() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();
            Task task = new Task();
            task.setId(taskId);

            when(taskRepository.findByTaskListIdAndId(listId, taskId))
                    .thenReturn(Optional.of(task));

            Optional<Task> result = taskServiceImpl.getTask(listId, taskId);

            assertTrue(result.isPresent());
            assertEquals(taskId, result.get().getId());
        }

        @Test
        @DisplayName("Deve retornar vazio quando Task não for achada")
        void shouldReturnEmptyWhenTaskNotFound() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            when(taskRepository.findByTaskListIdAndId(listId, taskId))
                    .thenReturn(Optional.empty());

            Optional<Task> result = taskServiceImpl.getTask(listId, taskId);

            assertTrue(result.isEmpty());
        }
    }


    @Nested
    class updateTaskTests {

        @Test
        @DisplayName("Deve atualizar Task com sucesso")
        void shouldUpdateTaskSuccessfully() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            Task existing = new Task();
            existing.setId(taskId);

            Task update = new Task();
            update.setTitle("Updated Title");
            update.setDescription("Updated Desc");
            update.setPriority(TaskPriority.HIGH);
            update.setStatus(TaskStatus.OPEN);

            when(taskRepository.findByTaskListIdAndId(listId, taskId))
                    .thenReturn(Optional.of(existing));

            when(taskRepository.save(any(Task.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Task result = taskServiceImpl.UpdateTask(listId, taskId, update);

            assertEquals("Updated Title", result.getTitle());
            assertEquals("Updated Desc", result.getDescription());
            assertEquals(TaskPriority.HIGH, result.getPriority());
            assertEquals(TaskStatus.OPEN, result.getStatus());

            verify(taskRepository, times(1)).save(existing);
        }

        @Test
        @DisplayName("Deve lançar exceão quando o Id é diferente")
        void shouldThrowWhenIdMismatch() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            Task task = new Task();
            task.setId(UUID.randomUUID());

            assertThrows(IllegalArgumentException.class, () -> {
                taskServiceImpl.UpdateTask(listId, taskId, task);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção quando status é null")
        void shouldThrowWhenStatusIsNull() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            Task task = new Task();
            task.setStatus(null);
            task.setPriority(TaskPriority.MEDIUM);

            assertThrows(IllegalArgumentException.class, () -> {
                taskServiceImpl.UpdateTask(listId, taskId, task);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção quando prioridade é nula")
        void shouldThrowWhenPriorityIsNull() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            Task task = new Task();
            task.setStatus(TaskStatus.OPEN);
            task.setPriority(null);

            assertThrows(IllegalArgumentException.class, () -> {
                taskServiceImpl.UpdateTask(listId, taskId, task);
            });
        }

        @Test
        @DisplayName("Deve lançar exceção quando a Task não é achada")
        void shouldThrowWhenTaskNotFound() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            Task task = new Task();
            task.setStatus(TaskStatus.OPEN);
            task.setPriority(TaskPriority.MEDIUM);

            when(taskRepository.findByTaskListIdAndId(listId, taskId))
                    .thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> {
                taskServiceImpl.UpdateTask(listId, taskId, task);
            });
        }
    }


    @Nested
    class deleteTaskTests {

        @Test
        @DisplayName("Deve deletar Task com sucesso")
        void shouldDeleteTaskSuccessfully() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            doNothing().when(taskRepository)
                    .deleteByTaskListIdAndId(listId, taskId);

            taskServiceImpl.deleteTask(listId, taskId);

            verify(taskRepository, times(1))
                    .deleteByTaskListIdAndId(listId, taskId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando Repositório falhar")
        void shouldThrowWhenRepositoryFails() {
            UUID listId = UUID.randomUUID();
            UUID taskId = UUID.randomUUID();

            doThrow(new RuntimeException("DB Fail"))
                    .when(taskRepository)
                    .deleteByTaskListIdAndId(listId, taskId);

            assertThrows(RuntimeException.class, () -> {
                taskServiceImpl.deleteTask(listId, taskId);
            });

            verify(taskRepository, times(1))
                    .deleteByTaskListIdAndId(listId, taskId);
        }
    }
}
