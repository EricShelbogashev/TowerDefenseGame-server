package ru.nsu.shelbogashev.tdgserver.server.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Jacksonized
public class Lobby {
    String id;

    Long createdAt;

    String adminUsername;

    List<String> members;

    /**
     * @param username deleted user.
     * @return false    if user is admin of lobby or not exists like a member.
     * true    if deleted.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean removeMember(String username) {
        if (username.equals(adminUsername)) {
            return false;
        }

        return members.remove(username);
    }

    public List<String> getMembers() {
        if (members == null) {
            members = new ArrayList<>();
        }
        return members;
    }

    public List<String> getMembers(boolean withAdmin) {
        if (!withAdmin) {
            return getMembers();
        }

        List<String> tmp = new ArrayList<>(members);
        tmp.add(adminUsername);
        return tmp;
    }
}