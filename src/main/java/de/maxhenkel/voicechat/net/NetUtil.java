package de.maxhenkel.voicechat.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class NetUtil {
    public static void writeUUID(DataOutputStream os, UUID uuid) throws IOException {
        os.writeLong(uuid.getMostSignificantBits());
        os.writeLong(uuid.getLeastSignificantBits());
    }

    public static void writeComponent(DataOutputStream os, BaseComponent... text) throws IOException {
        writeString(os, ComponentSerializer.toString(text));
    }

    public static void writeString(DataOutputStream os, String s) throws IOException {
        byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
        writeVarInt(os, abyte.length);
        os.write(abyte);
    }

    public static void writeVarInt(DataOutputStream os, int value) throws IOException {
        while ((value & -128) != 0) {
            os.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        os.writeByte(value);
    }

    public static void writeByteArray(DataOutputStream os, byte[] value) throws IOException {
        writeVarInt(os, value.length);
        os.write(value);
    }

    public static UUID readUUID(DataInputStream is) throws IOException {
        long msb = is.readLong();
        long lsb = is.readLong();
        return new UUID(msb, lsb);
    }

    public static int readVarInt(DataInputStream is) throws IOException {
        int value = 0;
        int byteCount = 0;
        byte b;
        do {
            b = is.readByte();
            value |= (b & 127) << byteCount++ * 7;
            if (byteCount > 5) {
                throw new IOException("invalid data, varint has too many bytes");
            }
        } while ((b & 128) == 128);
        return value;
    }

    public static byte[] readByteArray(DataInputStream is) throws IOException {
        int numBytes = readVarInt(is);
        byte[] value = new byte[numBytes];
        is.readFully(value);
        return value;
    }
}
