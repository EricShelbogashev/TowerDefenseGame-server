package ru.nsu.shelbogashev.tdgserver.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage;
import ru.nsu.shelbogashev.tdgserver.server.repository.UserRepository;
import ru.nsu.shelbogashev.tdgserver.server.rest.User;
import ru.nsu.shelbogashev.tdgserver.service.UserService;

import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.server.message.ResponseMessage.SUCCESSFUL_REGISTRATION;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    public boolean authorize(User user) {
        String encoded = passwordEncoder.encode(user.getPassword());
        User orig = userRepository.findByUsername(user.getUsername()).orElse(null);
        if (orig == null) {
            throw new BadCredentialsException(ResponseMessage.USER_NOT_FOUND);
        }
        return orig.getPassword().equals(encoded);
    }

    @Override
    public String register(User user) {
        // TODO: Add input validation.
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User registeredUser = userRepository.save(user);

        log.info("register() : registered " + registeredUser);

        return SUCCESSFUL_REGISTRATION;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<User> result = userRepository.findByUsername(username);

        log.info("findByUsername() : found " + result);

        return result;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        // TODO: Add error throwing in case of failure.
        Optional<User> result = userRepository.findUserById(id);

        log.info("findById() : found " + result);

        return result;
    }

}
