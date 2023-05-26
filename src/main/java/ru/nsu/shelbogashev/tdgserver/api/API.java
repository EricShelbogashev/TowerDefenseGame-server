package ru.nsu.shelbogashev.tdgserver.api;

public class API {
    public static class AUTH {
        public static final String LOGIN = "/auth/login";
        public static final String REGISTER = "/auth/register";
    }

    public static class STOMP {
        public static final String REGISTRY = "/websocket";
        public static final String FETCH_LOBBY_DESTROYED = "/topic/lobby.{lobby_id}.destroyed";
        public static final String API_TOWER_CREATE = "/api/game.create.tower";
        public static final String FETCH_LOBBY_UPDATED = "/topic/lobby.{lobby_id}.updated";
    }

    public static class USER {
        public static final String GET_FRIENDS = "/user/friends/get";
        public static final String GET_FRIENDS_ONLINE = "/user/friends/get/online";
        public static final String CREATE_LOBBY = "/user/lobby/create";
        public static final String FETCH_INVITE_FRIEND = "/topic/lobby.invitation";
        public static final String FETCH_ONLINE_FRIENDS = "/topic/friend.online.all";
        public static final String API_INVITE_FRIEND = "/api/lobby.invite.friend";
        public static final String API_INVITE_ACCEPT = "/user/lobby/accept";
    }

    public static class GAME {
        public static final String API_GAME_START = "/api/game.start";
        public static final String API_TOWER_CREATE = "/api/game.create.tower";
        private static final String TOPIC_FIELD_RECEIVED = "/topic/game.field.{lobby_id}.received";
        private static final String TOPIC_GAME_START = "/topic/game.{lobby_id}.start";

        public static String getTopicFieldReceived(String lobbyId) {
            return TOPIC_FIELD_RECEIVED.replace("{lobby_id}", lobbyId);
        }

        public static String getTopicGameStart(String lobbyId) {
            return TOPIC_GAME_START.replace("{lobby_id}", lobbyId);
        }
    }
}
