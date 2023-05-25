package ru.nsu.shelbogashev.tdgserver.server.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.shelbogashev.tdgserver.server.model.Relation;

import java.util.List;
import java.util.Optional;

public interface RelationRepository extends JpaRepository<Relation, Long> {
    @Transactional
    void deleteByFromIdAndToId(Long fromId, Long toId);

    List<Relation> findAllByFromId(Long fromId);

    Optional<Relation> findByFromId(Long fromId);

    Optional<Relation> findByFromIdAndToId(Long fromId, Long toId);
}
