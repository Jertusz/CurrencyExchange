package pl.szadejko.api.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import pl.szadejko.api.controller.CrudController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/users")
public class UserController extends CrudController<User> {
    UserService userService;

    protected UserController(UserService service) {
        super(service);
        this.userService = service;
    }

    // ALL ROLES MODIFICATION
    @PostMapping("/{id}/role/{role}")
    public ResponseEntity addRole(@PathVariable Long id, @PathVariable String role) {
        try {
            User user = userService.getById(id);
            user.addAuthority(() -> role);
            userService.createOrUpdate(user);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/role/{role}")
    public ResponseEntity setRole(@PathVariable Long id, @PathVariable String role) {
        try {
            User user = userService.getById(id);
            user.setStringAuthority(role);
            userService.createOrUpdate(user);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}/role/{role}")
    public ResponseEntity deleteRole(@PathVariable Long id, @PathVariable String role) {
        try {
            User user = userService.getById(id);
            user.removeAuthority(() -> role);
            userService.createOrUpdate(user);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Function<User, Map<String, Object>> transformToDTO() {
        return user -> {
            var payload = new LinkedHashMap<String, Object>();
            payload.put("id", user.getId());
            payload.put("username", user.getUsername());
            payload.put("authorities", user.getAuthorities().stream().map(GrantedAuthority::getAuthority));

            return payload;
        };
    }
}
