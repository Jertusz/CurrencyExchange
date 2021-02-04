package pl.szadejko.exchangeapi;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.szadejko.exchangeapi.security.User;
import pl.szadejko.exchangeapi.security.UserController;
import pl.szadejko.exchangeapi.security.UserService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(authorities = {"ROLE_ADMIN"})
@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserController userController;

    @MockBean
    UserService userService;

    @MockBean
    AuthenticationManager authenticationManager;


    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        Mockito.verify(userService).delete(1L);
    }

    @Test
    public void getAllUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        Mockito.verify(userService).getAll();
    }

    @Test
    public void getUserByID() throws Exception {
        User user = Mockito.spy(new User("admin", "password", "ROLE_USER"));
        Mockito.when(userService.getById(1L)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")).andExpect(status().isOk());
        Mockito.verify(userService).getById(1L);
    }

    @Test
    public void getNonExistingUserByID() throws Exception {
        Mockito.when(userService.getById(1L)).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")).andExpect(status().isInternalServerError());
        Mockito.verify(userService).getById(1L);
    }

    @Test
    public void modifyUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/users").contentType(MediaType.APPLICATION_JSON).content("{\"id\":\"1\", \"password\":\"new\"}")).andExpect(status().isAccepted());
        Mockito.verify(userService).createOrUpdate(Mockito.any(User.class));
    }

    // Test for adding roles
    @Test
    public void addRoleToUser() throws Exception {
        User user = Mockito.spy(new User("admin", "admin", ""));
        Mockito.when(userService.getById(1L)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/role/ROLE_USER").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        Mockito.verify(user).addAuthority(Mockito.any(GrantedAuthority.class));
        Mockito.verify(userService).createOrUpdate(Mockito.any(User.class));
    }

    @Test
    public void setUserRole() throws Exception {
        User user = Mockito.spy(new User("admin", "admin", ""));
        Mockito.when(userService.getById(1L)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/role/ROLE_USER").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        Mockito.verify(user).setStringAuthority("ROLE_USER");
        Mockito.verify(userService).createOrUpdate(Mockito.any(User.class));
    }

    @Test
    public void deleteUserRole() throws Exception {
        User user = Mockito.spy(new User("admin", "admin", "ROLE_USER"));
        Mockito.when(userService.getById(1L)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/role/ROLE_USER").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isAccepted());
        Mockito.verify(user).removeAuthority(Mockito.any(GrantedAuthority.class));
        Mockito.verify(userService).createOrUpdate(Mockito.any(User.class));
        Assert.assertEquals("", user.getAuthority());
    }

    @Test
    public void addDeleteModifyRoleForNonExistingUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/role/ROLE_USER").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/role/ROLE_USER").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        mockMvc.perform(MockMvcRequestBuilders.put("/users/1/role/ROLE_USER").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }
}
