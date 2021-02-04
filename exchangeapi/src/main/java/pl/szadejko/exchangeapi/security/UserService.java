package pl.szadejko.exchangeapi.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.szadejko.exchangeapi.service.CrudService;


@Service
public class UserService extends CrudService<User> implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        super(userRepository);
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findAll()
                .stream()
                .filter(user -> user.getUsername().equals(username))
                .findAny()
                .orElse(null);
    }

    @Override
    public User createOrUpdate(User updateEntity) {
        // Creating user
        User userInDb = loadUserByUsername(updateEntity.getUsername());
        if (userInDb == null) {
            GrantedAuthority defaultAuth = () -> "ROLE_USER";
            String encodedPassword = passwordEncoder.encode(updateEntity.getPassword());

            updateEntity.setPassword(encodedPassword);
            updateEntity.addAuthority(defaultAuth);
            System.out.println(updateEntity.getPassword());

            return repository.save(updateEntity);
        }
        // Updating user
        else {
            String newEncodedPassword = passwordEncoder.encode(updateEntity.getPassword());
            userInDb.setPassword(newEncodedPassword);
            userInDb.setStringAuthority(updateEntity.getAuthority());

            return repository.save(userInDb);
        }
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
