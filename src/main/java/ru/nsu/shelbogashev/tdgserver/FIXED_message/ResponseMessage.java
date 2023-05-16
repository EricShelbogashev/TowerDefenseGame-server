package ru.nsu.shelbogashev.tdgserver.FIXED_message;

public class ResponseMessage {
    public static String USER_NOT_FOUND = "user not found";
    public static String LOBBY_NOT_FOUND = "lobby not found";
    public static String USER_NOT_IN_A_LOBBY = "user is not in a lobby";
    public static String USER_NOT_AN_ADMIN = "user is not a lobby creator";
    public static String JWT_IS_EXPIRED_OR_INVALID = "jwt token is expired or invalid";
    public static String SYSTEM_ALREADY_HAS_USERNAME_ERROR = "username already exists";
    public static String SUCCESSFUL_REGISTRATION = "account registered successfully";
    public static String BAD_CREDENTIALS_ERROR = "incorrect username or password";
    public static String ILLEGAL_REQUEST_FORMAT_ERROR = "incorrect request format";
    public static String DEFAULT_ROLE_NOT_FOUND_ERROR = "default role is not found in repository";
    public static String UNEXPECTED_ERROR = "unexpected error occurred";
    public static String SYSTEM_ALREADY_HAS_FRIEND_INVITATION_ERROR = "friend invitation already sent";
    public static String SYSTEM_ALREADY_HAS_FRIENDSHIP_ERROR = "user already added to friends";
    public static String FRIEND_DELETED = "user deleted from friends";
    public static String FRIEND_INVITATION_SENT = "friend request sent";
    public static String FRIENDSHIP_CONCLUDED = "user added to friends";
    public static String FRIEND_INVITATION_DECLINE = "friend request declined";
    public static String FRIEND_INVITATION_ACCEPTED = "friend request accepted";
    public static String USER_SENT_FRIEND_INVITE_TO_HIMSELF_ERROR = "cannot add yourself to friends";
    public static String USER_REMOVE_HIMSELF_FROM_FRIENDS_ERROR = "cannot delete yourself from friends";
    public static String JWT_IS_INVALID = "jwt token is invalid";
    public static String AUTHORIZATION_HEADER_IS_MISSING_OR_INVALID = "Authorization header is missing or invalid";
    public static String SYSTEM_ALREADY_HAS_LOBBY_ERROR = "lobby already exists";
}
