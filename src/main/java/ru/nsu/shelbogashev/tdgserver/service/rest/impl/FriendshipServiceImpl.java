package ru.nsu.shelbogashev.tdgserver.service.rest.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.shelbogashev.tdgserver.exception.IllegalOperationException;
import ru.nsu.shelbogashev.tdgserver.model.rest.Friendship;
import ru.nsu.shelbogashev.tdgserver.model.rest.FriendshipInvitation;
import ru.nsu.shelbogashev.tdgserver.model.rest.User;
import ru.nsu.shelbogashev.tdgserver.repository.FriendshipInvitationRepository;
import ru.nsu.shelbogashev.tdgserver.repository.FriendshipRepository;
import ru.nsu.shelbogashev.tdgserver.repository.UserRepository;
import ru.nsu.shelbogashev.tdgserver.service.rest.FriendshipService;

import java.util.List;

import static ru.nsu.shelbogashev.tdgserver.message.ResponseMessage.*;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipInvitationRepository friendshipInvitationRepository;

    @Autowired
    public FriendshipServiceImpl(
            UserRepository userRepository,
            FriendshipRepository friendshipRepository,
            FriendshipInvitationRepository friendshipInvitationRepository
    ) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.friendshipInvitationRepository = friendshipInvitationRepository;
    }

    @Override
    public List<User> getAllIncomingInvitations(Long userId) {
        List<FriendshipInvitation> invitations = friendshipInvitationRepository.findAllByToId(userId);
        if (invitations == null) {
            return null;
        }
        return invitations.stream().
                map(it -> userRepository.findById(it.fromId).orElseThrow(
                                () -> new IllegalStateException(UNEXPECTED_ERROR)
                        )
                ).toList();
    }

    @Override
    public List<User> getAllOutgoingInvitations(Long userId) {
        List<FriendshipInvitation> invitations = friendshipInvitationRepository.findAllByFromId(userId);
        if (invitations == null) {
            return null;
        }
        return invitations.stream().
                map(it -> userRepository.findById(it.toId).orElseThrow(
                        () -> new IllegalStateException(UNEXPECTED_ERROR)
                )).toList();
    }

    public void acceptInvitation(User accepter, User friendCandidate) {
        friendshipInvitationRepository.deleteByFromIdAndToId(friendCandidate.getId(), accepter.getId());
        friendshipInvitationRepository.deleteByFromIdAndToId(accepter.getId(), friendCandidate.getId());
        Friendship friendship = new Friendship();
        friendship.userId = accepter.getId();
        friendship.friendId = friendCandidate.getId();
        friendshipRepository.saveAndFlush(friendship);
        friendship = new Friendship();
        friendship.userId = friendCandidate.getId();
        friendship.friendId = accepter.getId();
        friendshipRepository.saveAndFlush(friendship);
    }

    @Override
    public String declineInvitation(User decliner, User friendCandidate) {
        friendshipInvitationRepository.deleteByFromIdAndToId(friendCandidate.getId(), decliner.getId());
        return FRIEND_INVITATION_DECLINE;
    }

    @Override
    public String sendInvitation(User sender, User possibleFriend) {
        if (friendshipInvitationRepository.findByFromIdAndToId(sender.getId(), possibleFriend.getId()).isPresent()) {
            throw new IllegalOperationException(SYSTEM_ALREADY_HAS_FRIEND_INVITATION_ERROR);
        }
        if (friendshipRepository.findByUserIdAndFriendId(sender.getId(), possibleFriend.getId()).isPresent()) {
            throw new IllegalOperationException(SYSTEM_ALREADY_HAS_FRIENDSHIP_ERROR);
        }
        if (friendshipInvitationRepository.findByFromIdAndToId(possibleFriend.getId(), sender.getId()).isPresent()) {
            acceptInvitation(sender, possibleFriend);
            return FRIENDSHIP_CONCLUDED;
        }
        FriendshipInvitation friendInvitation = new FriendshipInvitation();
        friendInvitation.toId = possibleFriend.getId();
        friendInvitation.fromId = sender.getId();
        friendshipInvitationRepository.save(friendInvitation);
        return FRIEND_INVITATION_SENT;
    }

    @Override
    public List<User> getAllFriends(User user) {
        List<Friendship> friends = friendshipRepository.findAllByUserId(user.getId());
        return friends.stream().
                map(it -> userRepository.findById(it.friendId).orElseThrow(
                        () -> new IllegalStateException(UNEXPECTED_ERROR)))
                .toList();
    }

    @Override
    public String removeFriend(User deleter, User removedFriend) {
        friendshipRepository.deleteByUserIdAndFriendId(deleter.getId(), removedFriend.getId());
        friendshipRepository.deleteByUserIdAndFriendId(removedFriend.getId(), deleter.getId());
        return FRIEND_DELETED;
    }
}
