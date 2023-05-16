package ru.nsu.shelbogashev.tdgserver.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class User {

    public enum Status {
        IN_MENU, IN_LOBBY, IN_GAME, OFFLINE
    }

    @NonNull String username;
    @Builder.Default
    @NonNull Status status = Status.OFFLINE;
    @Builder.Default
    @NonNull String websocketSessionId = "";
}