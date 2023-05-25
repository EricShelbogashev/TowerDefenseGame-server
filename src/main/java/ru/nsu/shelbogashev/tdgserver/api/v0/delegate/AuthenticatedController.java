package ru.nsu.shelbogashev.tdgserver.api.v0.delegate;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.nsu.shelbogashev.tdgserver.server.dto.Mapper;
import ru.nsu.shelbogashev.tdgserver.server.model.User;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtUser;

@Log4j2
public class AuthenticatedController {
    protected final UserDetailsService userDetailsService;

    public AuthenticatedController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public User getCurrentUser() {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("getCurrentUser() : jwtUser=" + jwtUser);
        return Mapper.toUser(jwtUser);
    }
}
