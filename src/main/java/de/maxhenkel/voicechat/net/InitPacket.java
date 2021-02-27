package de.maxhenkel.voicechat.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class InitPacket {

    private UUID secret;
    private int serverPort;
    private int sampleRate;
    private int mtuSize;
    private double voiceChatDistance;
    private double voiceChatFadeDistance;
    private int keepAlive;

    public InitPacket(UUID secret, int serverPort, int sampleRate, int mtuSize, double voiceChatDistance, double voiceChatFadeDistance, int keepAlive) {
        this.secret = secret;
        this.serverPort = serverPort;
        this.sampleRate = sampleRate;
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

    public int getSampleRate() {
        return sampleRate;
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

    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeUUID(buf, secret);
        NetUtil.writeVarInt(buf, serverPort);
        NetUtil.writeVarInt(buf, sampleRate);
        NetUtil.writeVarInt(buf, mtuSize);
        buf.writeDouble(voiceChatDistance);
        buf.writeDouble(voiceChatFadeDistance);
        NetUtil.writeVarInt(buf, keepAlive);
    }

}
