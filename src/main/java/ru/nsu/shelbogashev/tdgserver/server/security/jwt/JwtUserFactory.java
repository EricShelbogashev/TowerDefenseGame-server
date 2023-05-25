package ru.nsu.shelbogashev.tdgserver.server.security.jwt;

import ru.nsu.shelbogashev.tdgserver.server.model.User;

public final class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

}
