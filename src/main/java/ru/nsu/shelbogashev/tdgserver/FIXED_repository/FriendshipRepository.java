package ru.nsu.shelbogashev.tdgserver.FIXED_repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.shelbogashev.tdgserver.model.rest.Friendship;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Transactional
    void deleteByUserIdAndFriendId(Long userId, Long friendId);

    List<Friendship> findAllByUserId(Long userId);

    Optional<Friendship> findByUserId(Long userId);

    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);
}
