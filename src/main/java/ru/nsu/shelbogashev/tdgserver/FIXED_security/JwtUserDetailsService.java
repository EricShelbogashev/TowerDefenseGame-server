package ru.nsu.shelbogashev.tdgserver.FIXED_security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;
import ru.nsu.shelbogashev.tdgserver.FIXED_security.jwt.JwtUser;
import ru.nsu.shelbogashev.tdgserver.FIXED_security.jwt.JwtUserFactory;
import ru.nsu.shelbogashev.tdgserver.service.UserService;

import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.FIXED_message.ResponseMessage.USER_NOT_FOUND;

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
        User user = Optional.of(username)
                .flatMap(userService::findByUsername)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("loadUserByUsername() : loaded user with login " + username);
        return jwtUser;
    }
}
