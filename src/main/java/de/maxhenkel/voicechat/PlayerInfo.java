package de.maxhenkel.voicechat;

import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;

public class PlayerInfo {

    private final UUID uuid;
    private final BaseComponent name;

    public PlayerInfo(UUID uuid, BaseComponent name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BaseComponent getName() {
        return name;
    }

}
