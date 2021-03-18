package de.maxhenkel.voicechat.net;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PlayerStatePacket implements Packet<PlayerStatePacket> {

    public static final String PLAYER_STATE = Voicechat.MODID + ":" + "player_state";

    private UUID uuid;
    private PlayerState playerState;

    public PlayerStatePacket() {

    }

    public PlayerStatePacket(UUID uuid, PlayerState playerState) {
        this.uuid = uuid;
        this.playerState = playerState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getID() {
        return PLAYER_STATE;
    }

    @Override
    public PlayerStatePacket fromBytes(DataInputStream buf) throws IOException {
        uuid = NetUtil.readUUID(buf);
        playerState = new PlayerState(buf.readBoolean(), buf.readBoolean());
        return this;
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeUUID(buf, uuid);
        buf.writeBoolean(playerState.isDisabled());
        buf.writeBoolean(playerState.isDisconnected());
    }

}
