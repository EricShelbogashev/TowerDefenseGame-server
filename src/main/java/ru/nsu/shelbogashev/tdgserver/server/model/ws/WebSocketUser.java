package ru.nsu.shelbogashev.tdgserver.server.model.ws;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.nsu.shelbogashev.tdgserver.server.model.Lobby;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSocketUser implements Serializable {

    String sessionId;

    String username;

    @Builder.Default
    Status status = Status.IN_MENU;

    @Builder.Default
    String lobbyId = null;

    public void leaveLobby() {
        lobbyId = null;
        status = Status.IN_MENU;
    }

    public Lobby createLobby() {
        status = Status.IN_LOBBY;
        Lobby lobby = Lobby.builder()
                .id(UUID.randomUUID().toString())
                .adminUsername(username)
                .createdAt(System.currentTimeMillis())
                .build();
        lobbyId = lobby.getId();
        return lobby;
    }

    public void joinLobby(Lobby lobby) {
        status = Status.IN_LOBBY;
        this.lobbyId = lobby.getId();
    }
}
