package pl.szadejko.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.szadejko.api.service.CurrencyService;

@RestController
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    UserService userService;

    @Autowired
    CurrencyService currencyService;

    @PostMapping
    public ResponseEntity register(@RequestBody User user) {
        try {
            if (userService.loadUserByUsername(user.getUsername()) != null) {
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        userService.createOrUpdate(user);
        currencyService.newAccount(user);
        return new ResponseEntity(HttpStatus.OK);
    }
}
