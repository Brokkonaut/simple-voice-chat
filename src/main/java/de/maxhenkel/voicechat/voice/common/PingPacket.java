package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.net.NetUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PingPacket implements Packet<PingPacket> {

    private UUID id;
    private long timestamp;

    public PingPacket(UUID id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public PingPacket() {

    }

    public long getTimestamp() {
        return timestamp;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public PingPacket fromBytes(DataInputStream buf) throws IOException {
        PingPacket soundPacket = new PingPacket();
        soundPacket.id = NetUtil.readUUID(buf);
        soundPacket.timestamp = buf.readLong();
        return soundPacket;
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeUUID(buf, id);
        buf.writeLong(timestamp);
    }
}
