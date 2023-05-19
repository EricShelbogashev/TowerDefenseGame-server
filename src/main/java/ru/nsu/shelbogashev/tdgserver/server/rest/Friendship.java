package ru.nsu.shelbogashev.tdgserver.server.rest;

import jakarta.persistence.*;

@Entity
@Table(name = "friends")
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "user_id")
    public Long userId;

    @Column(name = "friend_id")
    public Long friendId;
}
