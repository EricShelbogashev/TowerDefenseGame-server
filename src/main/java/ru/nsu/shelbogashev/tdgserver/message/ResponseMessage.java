package ru.nsu.shelbogashev.tdgserver.message;

public class ResponseMessage {
    public static String USER_NOT_FOUND = "пользователь не найден";
    public static String JWT_IS_EXPIRED_OR_INVALID = "jwt токен просрочен или некорректен";
    public static String SYSTEM_ALREADY_HAS_USERNAME_ERROR = "имя пользователя уже занято";
    public static String SUCCESSFUL_REGISTRATION = "аккаунт зарегистрирован";
    public static String BAD_CREDENTIALS_ERROR = "неверный логин или пароль";
    public static String ILLEGAL_REQUEST_FORMAT_ERROR = "некорректная структура запроса";
    public static String DEFAULT_ROLE_NOT_FOUND_ERROR = "отсутствует роль по умолчанию в репозитории";
    public static String UNEXPECTED_ERROR = "непредвиденная ошибка";
    public static String SYSTEM_ALREADY_HAS_FRIEND_INVITATION_ERROR = "приглашение в друзья уже отправлено";
    public static String SYSTEM_ALREADY_HAS_FRIENDSHIP_ERROR = "пользователь уже добавлен в друзья";
    public static String FRIEND_DELETED = "пользователь удален из друзей";
    public static String FRIEND_INVITATION_SENT = "заявка в друзья отправлена";
    public static String FRIENDSHIP_CONCLUDED = "пользователь добавлен в друзья";
    public static String FRIEND_INVITATION_DECLINE = "заявка в друзья отклонена";
    public static String FRIEND_INVITATION_ACCEPTED = "заявка в друзья принята";
    public static String USER_SENT_FRIEND_INVITE_TO_HIMSELF_ERROR = "невозможно добавить в друзья самого себя";
    public static String USER_REMOVE_HIMSELF_FROM_FRIENDS_ERROR = "невозможно удалить из друзей самого себя";
    public static String JWT_IS_INVALID = "jwt токен некорректен";
    public static String AUTHORIZATION_HEADER_IS_MISSING_OR_INVALID = "отсутствует или некорректен заголовок Authorization";
    public static String SYSTEM_ALREADY_HAS_LOBBY_ERROR = "лобби уже создано";
}
