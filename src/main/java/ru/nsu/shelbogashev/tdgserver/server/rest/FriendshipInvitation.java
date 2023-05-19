package ru.nsu.shelbogashev.tdgserver.server.rest;

import jakarta.persistence.*;

@Entity
@Table(name = "friend_invitation")
public class FriendshipInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "from_id")
    public Long fromId;

    @Column(name = "to_id")
    public Long toId;
}
