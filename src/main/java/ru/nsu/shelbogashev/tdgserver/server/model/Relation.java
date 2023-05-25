package ru.nsu.shelbogashev.tdgserver.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "relations")
public class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "from_id")
    public Long fromId;

    @Column(name = "to_id")
    public Long toId;
}
