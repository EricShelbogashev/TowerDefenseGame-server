package ru.nsu.shelbogashev.tdgserver.api.v0;

public class ApiHelper {
    private static final String KEY = "{lobby_id}";

    public static String makeLobbyDestination(String destination, String lobbyId) {
        return destination.replace(KEY, lobbyId);
    }
}
