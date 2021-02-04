package pl.szadejko.api.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login", "/register", "/move").permitAll()
                .antMatchers("/users/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/currency/deposit/**").authenticated()
                .antMatchers(HttpMethod.POST, "/currency/withdraw/**").authenticated()
                .antMatchers(HttpMethod.GET, "/balance").authenticated()
                .antMatchers(HttpMethod.POST, "/currency/convert/**").authenticated()
                .antMatchers(HttpMethod.GET, "/**").hasAnyRole("USER", "PRIVILEGED", "ADMIN")
                .antMatchers("/**").hasAnyRole("PRIVILEGED", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();
    }
}
