package ru.nsu.shelbogashev.tdgserver.service.rest;

import ru.nsu.shelbogashev.tdgserver.model.rest.User;

import java.util.List;

public interface FriendshipService {
    List<User> getAllIncomingInvitations(Long userId);

    List<User> getAllOutgoingInvitations(Long userId);

    String declineInvitation(User decliner, User friendCandidate);

    String sendInvitation(User sender, User possibleFriend);

    List<User> getAllFriends(User user);

    String removeFriend(User deleter, User removedFriend);
}
