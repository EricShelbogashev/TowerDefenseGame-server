package ru.nsu.shelbogashev.tdgserver.FIXED_repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.shelbogashev.tdgserver.model.rest.FriendshipInvitation;

import java.util.List;
import java.util.Optional;

public interface FriendshipInvitationRepository extends JpaRepository<FriendshipInvitation, Long> {
    List<FriendshipInvitation> findAllByFromId(Long fromId);

    List<FriendshipInvitation> findAllByToId(Long toId);

    Optional<FriendshipInvitation> findByFromIdAndToId(Long fromId, Long toId);

    @Transactional
    void deleteByFromIdAndToId(Long fromId, Long toId);
}
