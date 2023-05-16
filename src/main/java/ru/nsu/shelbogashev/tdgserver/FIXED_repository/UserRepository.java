package ru.nsu.shelbogashev.tdgserver.FIXED_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findUserById(Long id);
}
