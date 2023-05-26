package ru.nsu.shelbogashev.tdgserver.server.towerdefense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameStartDto {
    int length;
}
