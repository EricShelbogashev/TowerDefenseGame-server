package ru.nsu.shelbogashev.tdgserver.server.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.shelbogashev.tdgserver.server.model.Invitation;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findAllByFromId(Long fromId);

    List<Invitation> findAllByToId(Long toId);

    Optional<Invitation> findByFromIdAndToId(Long fromId, Long toId);

    @Transactional
    void deleteByFromIdAndToId(Long fromId, Long toId);
}
