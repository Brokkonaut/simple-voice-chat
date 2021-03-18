package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.net.NetUtil;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MicPacket implements Packet<MicPacket> {

    private byte[] data;
    private long sequenceNumber;

    public MicPacket(byte[] data, long sequenceNumber) {
        this.data = data;
        this.sequenceNumber = sequenceNumber;
    }

    public MicPacket() {

    }

    public byte[] getData() {
        return data;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public MicPacket fromBytes(DataInputStream buf) throws IOException {
        MicPacket soundPacket = new MicPacket();
        soundPacket.data = NetUtil.readByteArray(buf);
        soundPacket.sequenceNumber = buf.readLong();
        return soundPacket;
    }

    @Override
    public void toBytes(DataOutputStream buf) throws IOException {
        NetUtil.writeByteArray(buf, data);
        buf.writeLong(sequenceNumber);
    }
}
