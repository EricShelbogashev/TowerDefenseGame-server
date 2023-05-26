package ru.nsu.shelbogashev.tdgserver.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStart {
    int length;
    Lobby lobby;
}
