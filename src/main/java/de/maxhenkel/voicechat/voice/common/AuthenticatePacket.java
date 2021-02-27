package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.net.NetUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AuthenticatePacket implements Packet<AuthenticatePacket> {

    private UUID playerUUID;
    private UUID secret;

    public AuthenticatePacket(UUID playerUUID, UUID secret) {
        this.playerUUID = playerUUID;
        this.secret = secret;
    }

    public AuthenticatePacket() {

    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getSecret() {
        return secret;
    }

    @Override
    public AuthenticatePacket fromBytes(DataInputStream buf) throws IOException {
        AuthenticatePacket packet = new AuthenticatePacket();
        packet.playerUUID = NetUtil.readUUID(buf);
        packet.secret = NetUtil.readUUID(buf);
        return packet;
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeUUID(buf, playerUUID);
        NetUtil.writeUUID(buf, secret);
    }
}
