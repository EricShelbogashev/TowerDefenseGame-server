package ru.nsu.shelbogashev.tdgserver.service.impl;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.server.exception.AuthException;
import ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage;
import ru.nsu.shelbogashev.tdgserver.server.repository.UserRepository;
import ru.nsu.shelbogashev.tdgserver.server.rest.User;
import ru.nsu.shelbogashev.tdgserver.server.security.jwt.JwtTokenProvider;
import ru.nsu.shelbogashev.tdgserver.service.AuthService;

import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.SUCCESSFUL_REGISTRATION;
import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.SYSTEM_ALREADY_HAS_USERNAME_ERROR;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    public String login(User user) throws AuthException {
        log.info("login() : user=" + user);
        User localUser = getUser(user);
        if (localUser == null) {
            log.info("login() : user not found : user=" + user);
            throw new AuthException(ResponseMessage.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(user.getPassword(), localUser.getPassword())) {
            log.info("login() : wrong password : user=" + user);
            throw new AuthException(ResponseMessage.INVALID_PASSWORD);
        }

        return tokenProvider.createToken(user);
    }

    // TODO: обработать ошибки.
    @Override
    public String register(User user) throws IllegalArgumentException, OptimisticLockingFailureException, AuthException {
        log.info("register() : user=" + user);

        User localUser = getUser(user);
        if (localUser != null) {
            throw new AuthException(SYSTEM_ALREADY_HAS_USERNAME_ERROR);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return SUCCESSFUL_REGISTRATION;
    }

    private @Nullable User getUser(@NotNull User user) {
        return Optional.of(user.getUsername()).flatMap(userRepository::findByUsername).orElse(null);
    }
}
