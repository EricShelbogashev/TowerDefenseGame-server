package ru.nsu.shelbogashev.tdgserver.server.message;

public class ResponseMessage {
    public static final String USER_IS_ALREADY_A_LOBBY_MEMBER = "user is already in lobby";
    public static final String USER_NOT_FOUND = "user not found";
    public static final String USER_NOT_IN_A_LOBBY = "user is not in a lobby";
    public static final String USER_NOT_AN_ADMIN = "user is not a lobby creator";
    public static final String JWT_IS_EXPIRED_OR_INVALID = "jwt token is expired or invalid";
    public static final String INCORRECT_PRESENTATION_OF_CREDENTIALS = "incorrect presentation of username or password";
    public static final String INVALID_PASSWORD = "invalid password";
    public static final String SYSTEM_ALREADY_HAS_USERNAME_ERROR = "username already exists";
    public static final String SUCCESSFUL_REGISTRATION = "account registered successfully";
    public static final String BAD_CREDENTIALS_ERROR = "incorrect username or password";
    public static final String ILLEGAL_REQUEST_FORMAT_ERROR = "incorrect request format";
    public static final String UNEXPECTED_ERROR = "unexpected error occurred";
    public static final String SYSTEM_ALREADY_HAS_FRIEND_INVITATION_ERROR = "friend invitation already sent";
    public static final String SYSTEM_ALREADY_HAS_FRIENDSHIP_ERROR = "user already added to friends";
    public static final String FRIEND_DELETED = "user deleted from friends";
    public static final String FRIEND_INVITATION_SENT = "friend request sent";
    public static final String FRIENDSHIP_CONCLUDED = "user added to friends";
    public static final String FRIEND_INVITATION_DECLINE = "friend request declined";
    public static final String SYSTEM_ALREADY_HAS_LOBBY_ERROR = "lobby already exists";
    public static final String G0_0_ERROR = "user repository is broken";
}

