package ru.nsu.shelbogashev.tdgserver.server.service;

import org.springframework.dao.OptimisticLockingFailureException;
import ru.nsu.shelbogashev.tdgserver.server.exception.AuthException;
import ru.nsu.shelbogashev.tdgserver.server.model.User;

public interface AuthService {
    String login(User user) throws AuthException;

    String register(User user) throws IllegalArgumentException, OptimisticLockingFailureException;
}
