package ru.nsu.shelbogashev.tdgserver.FIXED_security.jwt;

import ru.nsu.shelbogashev.tdgserver.model.rest.User;

public final class JwtUserFactory {

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

}
