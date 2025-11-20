package app.taskmanager.controller;

import app.taskmanager.controller.dto.CreateUserDto;
import app.taskmanager.entities.Role;
import app.taskmanager.entities.User;
import app.taskmanager.repositories.RoleRepository;
import app.taskmanager.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @Nested
    class newUser{
        @Test
        @DisplayName("Deve criar usuário com sucesso")
        void shouldCreateUserSuccessfully() {

            CreateUserDto dto = new CreateUserDto("john", "123");

            Role basicRole = new Role();
            basicRole.setRoleName("BASIC");

            when(roleRepository.findByRoleName("BASIC")).thenReturn(basicRole);
            when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("123")).thenReturn("encoded123");

            ResponseEntity<Void> response = userController.newUser(dto);

            assertEquals(200, response.getStatusCodeValue());

            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar criar usuário já existente")
        void shouldNotCreateUserIfAlreadyExists() {

            CreateUserDto dto = new CreateUserDto("john", "123");

            when(userRepository.findByUsername("john"))
                    .thenReturn(Optional.of(new User()));

            assertThrows(ResponseStatusException.class, () -> {
                userController.newUser(dto);
            });

            verify(userRepository, never()).save(any());
        }

    }



    @Nested
    class listUsers{
        @Test
        @DisplayName("Deve listar usuários com sucesso")
        void shouldListUsersSuccessfully() {

            User u1 = new User();
            u1.setUsername("john");

            User u2 = new User();
            u2.setUsername("mary");

            when(userRepository.findAll()).thenReturn(List.of(u1, u2));

            ResponseEntity<List<User>> response = userController.listUsers();

            assertEquals(200, response.getStatusCodeValue());
            assertEquals(2, response.getBody().size());
            assertEquals("john", response.getBody().get(0).getUsername());
            assertEquals("mary", response.getBody().get(1).getUsername());
        }
    }

}
