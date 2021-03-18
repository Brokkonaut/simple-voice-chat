package de.maxhenkel.voicechat.net;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.config.ServerConfig;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class InitPacket implements Packet<InitPacket> {

    public static final String SECRET = Voicechat.MODID + ":" + "secret";

    private UUID secret;
    private int serverPort;
    private ServerConfig.Codec codec;
    private int mtuSize;
    private double voiceChatDistance;
    private double voiceChatFadeDistance;
    private int keepAlive;

    public InitPacket() {

    }

    public InitPacket(UUID secret, int serverPort, ServerConfig.Codec codec, int mtuSize, double voiceChatDistance, double voiceChatFadeDistance, int keepAlive) {
        this.secret = secret;
        this.serverPort = serverPort;
        this.codec = codec;
        this.mtuSize = mtuSize;
        this.voiceChatDistance = voiceChatDistance;
        this.voiceChatFadeDistance = voiceChatFadeDistance;
        this.keepAlive = keepAlive;
    }

    public UUID getSecret() {
        return secret;
    }

    public int getServerPort() {
        return serverPort;
    }

    public ServerConfig.Codec getCodec() {
        return codec;
    }

    public int getMtuSize() {
        return mtuSize;
    }

    public double getVoiceChatDistance() {
        return voiceChatDistance;
    }

    public double getVoiceChatFadeDistance() {
        return voiceChatFadeDistance;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    @Override
    public String getID() {
        return SECRET;
    }

    @Override
    public InitPacket fromBytes(DataInputStream buf) {
        return this; // clientside only
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeUUID(buf, secret);
        buf.writeInt(serverPort);
        buf.writeByte(codec.ordinal());
        buf.writeInt(mtuSize);
        // NetUtil.writeVarInt(buf, serverPort);
        // NetUtil.writeVarInt(buf, sampleRate);
        // NetUtil.writeVarInt(buf, mtuSize);
        buf.writeDouble(voiceChatDistance);
        buf.writeDouble(voiceChatFadeDistance);
        buf.writeInt(keepAlive);
        // NetUtil.writeVarInt(buf, keepAlive);
    }

}
