package ru.nsu.shelbogashev.tdgserver.server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.HashMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Stats implements Serializable {
    @Builder.Default
    HashMap<String, Integer> moneys = null;
    @Builder.Default
    HashMap<String, Boolean> alive = null;
}
