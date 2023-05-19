package ru.nsu.shelbogashev.tdgserver.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtConfigurer;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtTokenProvider;

@Configuration
public class SecurityConfig {

    private static final String LOGIN_ENDPOINT = "/auth/authorize";
    private static final String REGISTER_ENDPOINT = "/auth/register";
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(LOGIN_ENDPOINT, REGISTER_ENDPOINT).permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationManager() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                // Null if {@link JwtTokenProvider#getAuthentication returned null}
                return authentication;
            }
        };
    }

}