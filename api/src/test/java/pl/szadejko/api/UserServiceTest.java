package pl.szadejko.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.szadejko.api.security.User;
import pl.szadejko.api.security.UserRepository;
import pl.szadejko.api.security.UserService;

import java.util.List;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    UserService userService;

    @MockBean
    UserRepository userRepository;

    @Before
    public void setup() {
        userService = Mockito.spy(new UserService(userRepository));
    }

    @Test
    public void addUser() {
        var user = new User("username", "password", "ROLE_USER");
        userService.createOrUpdate(user);
        Mockito.verify(userRepository).save(user);
    }

    @Test
    public void modifyUser() {
        var user = Mockito.spy(new User("username", "password", "ROLE_USER"));
        // This ensures that compartment "exists" in our db, hence goes to modify

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        userService.createOrUpdate(user);

        Mockito.verify(userService).loadUserByUsername(user.getUsername());
        Mockito.verify(userRepository).save(user);
    }
    // Tests for parametrized methods from CrudService
    @Test
    public void getUserByID() {
        userService.getById(1L);
        Mockito.verify(userRepository).findById(1L);
    }

    @Test
    public void deleteUserByID() {
        var user = new User("username", "password", "ROLE_USER");
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepository.findById(1L)).thenReturn(userOptional);
        userService.delete(1L);
        Mockito.verify(userRepository).findById(1L);
        Mockito.verify(userRepository).delete(userOptional.get());
    }

    @Test
    public void getAllUsers() {
        userService.getAll();
        Mockito.verify(userRepository).findAll();
    }
}