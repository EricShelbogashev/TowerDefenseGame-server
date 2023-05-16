package ru.nsu.shelbogashev.tdgserver.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;
import ru.nsu.shelbogashev.tdgserver.FIXED_repository.UserRepository;
import ru.nsu.shelbogashev.tdgserver.service.UserService;

import java.util.Optional;

import static ru.nsu.shelbogashev.tdgserver.FIXED_message.ResponseMessage.SUCCESSFUL_REGISTRATION;

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
    public String register(User user) {
        // TODO: Add input validation.
        // TODO: Add check for existence of a user with such email and nickname in the database separately.
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
