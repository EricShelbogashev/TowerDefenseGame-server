package ru.nsu.shelbogashev.tdgserver.service;

import ru.nsu.shelbogashev.tdgserver.server.rest.User;

import java.util.Optional;

public interface UserService {
    boolean authorize(User user);

    String register(User user) throws IllegalStateException;

    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id);
}
