package pl.szadejko.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.szadejko.api.service.DbEntity;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
public class User implements DbEntity, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String authority;

    public User(String username, String password, String authority) {
        this.username = username;
        this.password = password;
        this.authority = authority;
    }

    public User() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(authority.split(","))
                .map(String::trim)
                .filter(authority -> !authority.equals(""))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public void addAuthority(GrantedAuthority authority) {
        String formattedAuthority = authority.getAuthority().trim();
        String currentAuthority = this.authority == null ? "" : (this.authority + ",");
        String updatedAuthority = currentAuthority + formattedAuthority;
        this.authority = updatedAuthority;
    }

    public void removeAuthority(GrantedAuthority authority) {
        String formattedAuthority = authority.getAuthority().trim();
        String addedAuthority = this.authority.replace(formattedAuthority, "")
                .replace(",,", "")
                .trim();
        this.authority = addedAuthority;
    }

    public void setStringAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
