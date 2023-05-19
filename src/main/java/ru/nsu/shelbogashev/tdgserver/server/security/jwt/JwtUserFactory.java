package ru.nsu.shelbogashev.tdgserver.server.security.jwt;

import ru.nsu.shelbogashev.tdgserver.server.rest.User;

public final class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

}
