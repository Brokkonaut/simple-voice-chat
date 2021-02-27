package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.net.NetUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MicPacket implements Packet<MicPacket> {

    private byte[] data;

    public MicPacket(byte[] data) {
        this.data = data;
    }

    public MicPacket() {

    }

    public byte[] getData() {
        return data;
    }

    @Override
    public MicPacket fromBytes(DataInputStream buf) throws IOException {
        MicPacket soundPacket = new MicPacket();
        soundPacket.data = NetUtil.readByteArray(buf);
        return soundPacket;
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeByteArray(buf, data);
    }
}
