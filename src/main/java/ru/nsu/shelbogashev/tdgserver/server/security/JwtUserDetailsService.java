package ru.nsu.shelbogashev.tdgserver.server.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.server.rest.User;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtUser;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtUserFactory;
import ru.nsu.shelbogashev.tdgserver.service.UserService;

import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.USER_NOT_FOUND;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername() : loaded user with login " + username);
        User user = Optional.of(username)
                .flatMap(userService::findByUsername)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        return JwtUserFactory.create(user);
    }
}
