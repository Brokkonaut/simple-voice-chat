package de.maxhenkel.voicechat.net;

import de.maxhenkel.voicechat.PlayerInfo;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class PlayerListPacket {

    private List<PlayerInfo> players;

    public PlayerListPacket(List<PlayerInfo> players) {
        this.players = players;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }

    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeVarInt(buf, players.size());
        for (PlayerInfo info : players) {
            NetUtil.writeUUID(buf, info.getUuid());
            NetUtil.writeComponent(buf, info.getName());
        }
    }
}
