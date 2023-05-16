package ru.nsu.shelbogashev.tdgserver.service;

import ru.nsu.shelbogashev.tdgserver.model.rest.User;

import java.util.Optional;

public interface UserService {
    String register(User user) throws IllegalStateException;

    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id);
}
