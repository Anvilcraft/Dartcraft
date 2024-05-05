package ley.modding.dartcraft.api;

import java.util.UUID;

public interface IOwnedTile {
    UUID getOwner();

    AccessLevel getAccessLevel();
}
