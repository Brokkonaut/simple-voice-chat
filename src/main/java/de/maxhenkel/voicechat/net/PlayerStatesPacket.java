package de.maxhenkel.voicechat.net;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatesPacket implements Packet<PlayerStatesPacket> {

    private Map<UUID, PlayerState> playerStates;

    public static final String PLAYER_STATES = Voicechat.MODID + ":" + "player_states";

    public PlayerStatesPacket() {

    }

    public PlayerStatesPacket(Map<UUID, PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public Map<UUID, PlayerState> getPlayerStates() {
        return playerStates;
    }

    @Override
    public String getID() {
        return PLAYER_STATES;
    }

    @Override
    public PlayerStatesPacket fromBytes(DataInputStream buf) throws IOException {
        playerStates = new HashMap<>();
        int count = buf.readInt();
        for (int i = 0; i < count; i++) {
            playerStates.put(NetUtil.readUUID(buf), new PlayerState(buf.readBoolean(), buf.readBoolean()));
        }

        return this;
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        buf.writeInt(playerStates.size());
        for (Map.Entry<UUID, PlayerState> entry : playerStates.entrySet()) {
            NetUtil.writeUUID(buf, entry.getKey());
            buf.writeBoolean(entry.getValue().isDisabled());
            buf.writeBoolean(entry.getValue().isDisconnected());
        }
    }

}
