package ru.nsu.shelbogashev.tdgserver.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@AllArgsConstructor
@Jacksonized
public class User {

    @NonNull String username;

    @Builder.Default
    @NonNull Status status = Status.OFFLINE;

    @Builder.Default
    @NonNull String websocketSessionId = "";

    public enum Status {
        IN_MENU, IN_LOBBY, IN_GAME, OFFLINE
    }
}