package app.taskmanager.service.impl;

import app.taskmanager.entities.Role;
import app.taskmanager.entities.Task;
import app.taskmanager.entities.TaskList;
import app.taskmanager.entities.User;
import app.taskmanager.repositories.TaskListRepository;
import app.taskmanager.repositories.UserRepository;
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

@ExtendWith(MockitoExtension.class)
class TaskListServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskListServiceImpl taskListServiceImpl;

    @Captor
    private ArgumentCaptor<TaskList> tasklistArgumentCaptor;

    @Nested
    class createTaskList{
        @Test
        @DisplayName("Deve criar uma Task List com sucesos")
        void shouldCreateATasklistWithSuccess() {

            Role basicRole = new Role();
            basicRole.setRoleId(2L);

            User user = new User();
            user.setUserId(UUID.randomUUID());
            user.setUsername("UsuarioTeste");
            user.setPassword("123");
            user.setRoles(Set.of(basicRole));

            Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());


            var input1 = new TaskList(
                    null,
                    "Titulo teste",
                    "Descrição teste",
                    user,
                    null,
                    LocalDateTime.now(clock),
                    LocalDateTime.now(clock)
            );



            Mockito.when(taskListRepository.save(tasklistArgumentCaptor.capture())).thenReturn(input1);


            //Act
        var output  = taskListServiceImpl.createTasklist(input1, user);
            //Assert
            assertNotNull(output);
            var tasklistCaptured = tasklistArgumentCaptor.getValue();
            assertEquals(output.getId(), tasklistCaptured.getId());
            assertEquals(output.getTitle(), tasklistCaptured.getTitle());
            assertEquals(output.getDescription(), tasklistCaptured.getDescription());
            assertEquals(output.getUser(), tasklistCaptured.getUser());
            assertEquals(output.getTasks(), tasklistCaptured.getTasks());
            assertNotNull(tasklistCaptured.getCreated());
            assertNotNull(tasklistCaptured.getUpdated());





        }

        @Test
        @DisplayName("Deve lançar exceção quando erro ocorre")
        void shouldThrowExceptionWhenErrorOccurs(){
            Role basicRole = new Role();
            basicRole.setRoleId(2L);

            User user = new User();
            user.setUserId(UUID.randomUUID());
            user.setUsername("UsuarioTeste");
            user.setPassword("123");
            user.setRoles(Set.of(basicRole));

            doThrow(new RuntimeException()).when(taskListRepository).save(any());

            var input1 = new TaskList(
                    null,
                    "Titulo teste",
                    "Descrição teste",
                    user,
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            assertThrows(RuntimeException.class, () -> taskListServiceImpl.createTasklist(input1, user));

        }

        @Test
        @DisplayName("Deve lançar exceção quando Task List já tem um ID")
        void shouldThrowExceptionWhenTaskListHasAnId(){
            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());

            User user = new User();

            assertThrows(IllegalArgumentException.class, () -> taskListServiceImpl.createTasklist(taskList, user));


        }

        @Test
        @DisplayName("Deve lançar exceção quando o titulo for nulo")
        void shouldThrowExcpetionWhenTitleIsNull(){
            TaskList taskList = new TaskList();
            taskList.setTitle(null);

            User user = new User();

            assertThrows(IllegalArgumentException.class, () -> taskListServiceImpl.createTasklist(taskList, user));
        }

        @Test
        @DisplayName("Deve lançar exceção quando o titulo for vazio")
        void shouldThrowExceptionWhenTitleIsBlank(){
            TaskList taskList = new TaskList();
            taskList.setTitle(" ");

            User user = new User();

            assertThrows(IllegalArgumentException.class, () -> taskListServiceImpl.createTasklist(taskList, user));
        }

    }

    @Nested
    class listTaskLists{
        @Test
        @DisplayName ("Deve retornar as listas com sucesso")
        void shouldReturnTaskListsWithSuccess(){
            List<TaskList> mockList = List.of(new TaskList(), new TaskList());

            when(taskListRepository.findAll()).thenReturn(mockList);

            List<TaskList> result = taskListServiceImpl.listTaskLists();

            assertEquals(2, result.size());
            assertSame(result, mockList);


        }

        @Test
        @DisplayName("Deve lançar exceção quando erro ocorrer")
        void shouldThrowExceptionWhenErrorOccurs(){
            when(taskListRepository.findAll()).thenThrow(new RuntimeException("Erro ocorreu"));

            assertThrows(RuntimeException.class, () -> taskListServiceImpl.listTaskLists());
        }

        @Test
        @DisplayName("Deve retornar lista vazia")
        void shouldReturnTaskListEmpty(){
            List<TaskList> mockList = List.of();

            when(taskListRepository.findAll()).thenReturn(mockList);

            List<TaskList> result = taskListServiceImpl.listTaskLists();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class getTaskList{
        @Test
        @DisplayName("Deve retornar uma TaskList por meio do id")
        void shouldReturnTaskListById(){
            UUID id = UUID.randomUUID();
            TaskList mockList = new TaskList();
            mockList.setId(id);

            when(taskListRepository.findById(id)).thenReturn(Optional.of(mockList));



        }

        @Test
        @DisplayName("Deve lançar exceção quando id não existe")
                void shouldThrowExceptionWhenIdDoesNotExist(){
            UUID id = UUID.randomUUID();

            when(taskListRepository.findById(id)).thenReturn(Optional.empty());

            Optional<TaskList> result = taskListServiceImpl.getTaskList(id);

            assertTrue(result.isEmpty());
        }




    }

    @Nested
    class updateTaskList{

        @Test
        @DisplayName("Deve atualizar com sucesso a Task List")
        void shouldUpdateTaskListWithSuccess(){

            UUID id = UUID.randomUUID();
            TaskList taskListv1 = new TaskList();
            taskListv1.setId(id);
            taskListv1.setTitle("Titulo v1");
            taskListv1.setDescription("Desc v1");

            TaskList updatedTaskList = new TaskList();

            updatedTaskList.setTitle("Titulo v2");
            updatedTaskList.setDescription("Desc v2");

            when(taskListRepository.findById(id)).thenReturn(Optional.of(taskListv1));
            when(taskListRepository.save(taskListv1)).thenReturn(taskListv1);

            TaskList result = taskListServiceImpl.updateTaskList(id, updatedTaskList);

            assertEquals("Titulo v2", result.getTitle());
            assertEquals("Desc v2", result.getDescription());
            assertNotNull(result.getUpdated());
            verify(taskListRepository).save(taskListv1);


        }

        @Test
        @DisplayName("Deve lançar excessão quando id não existe")
        void shouldThrowExceptionWhenIdDoesntExist(){
            UUID id = UUID.randomUUID();
            TaskList taskList = new TaskList();

            when(taskListRepository.findById(id)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskListServiceImpl.updateTaskList(id, taskList));

            assertEquals("Id não encontrado", exception.getMessage());
            verify(taskListRepository, never()).save(any(TaskList.class));
        }

    }

    @Nested
    class deleteTaskList{

        @Test
        void shouldDeleteTaskListWithSuccess(){
            UUID id = UUID.randomUUID();

            doNothing().when(taskListRepository).deleteById(id);

            taskListServiceImpl.deleteTaskList(id);

            verify(taskListRepository, times(1)).deleteById(id);
        }

        @Test
        void shouldThrowExceptionWhenRepositoryFails() {
            // Arrange
            UUID id = UUID.randomUUID();

            doThrow(new RuntimeException("Database error"))
                    .when(taskListRepository)
                    .deleteById(id);

            // Act + Assert
            assertThrows(RuntimeException.class, () -> {
                taskListServiceImpl.deleteTaskList(id);
            });

            verify(taskListRepository, times(1)).deleteById(id);
        }
    }




    //Arrange
    //Act
    //Assert

}