package de.maxhenkel.voicechat.net;

import de.maxhenkel.voicechat.PlayerInfo;
import de.maxhenkel.voicechat.Voicechat;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class PlayerListPacket implements Packet<PlayerListPacket> {

    public static final String PLAYER_LIST = Voicechat.MODID + ":" + "player_list";

    private List<PlayerInfo> players;

    public PlayerListPacket() {

    }

    public PlayerListPacket(List<PlayerInfo> players) {
        this.players = players;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    @Override
    public String getID() {
        return PLAYER_LIST;
    }

    @Override
    public PlayerListPacket fromBytes(DataInputStream buf) {
        return this; // clientside only
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        buf.writeInt(players.size());
        for (PlayerInfo info : players) {
            NetUtil.writeUUID(buf, info.getUuid());
            NetUtil.writeComponent(buf, info.getName());
        }
    }
}
