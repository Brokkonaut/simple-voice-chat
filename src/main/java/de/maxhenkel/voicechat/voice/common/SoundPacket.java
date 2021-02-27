package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.net.NetUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SoundPacket implements Packet<SoundPacket> {

    private UUID sender;
    private byte[] data;

    public SoundPacket(UUID sender, byte[] data) {
        this.sender = sender;
        this.data = data;
    }

    public SoundPacket() {

    }

    public byte[] getData() {
        return data;
    }

    public UUID getSender() {
        return sender;
    }

    @Override
    public SoundPacket fromBytes(DataInputStream buf) throws IOException {
        SoundPacket soundPacket = new SoundPacket();
        soundPacket.sender = NetUtil.readUUID(buf);
        soundPacket.data = NetUtil.readByteArray(buf);
        return soundPacket;
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeUUID(buf, sender);
        NetUtil.writeByteArray(buf, data);
    }
}
