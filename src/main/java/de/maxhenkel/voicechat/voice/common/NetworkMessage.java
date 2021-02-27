package de.maxhenkel.voicechat.voice.common;

import de.maxhenkel.voicechat.net.NetUtil;
import de.maxhenkel.voicechat.voice.server.ClientConnection;
import de.maxhenkel.voicechat.voice.server.Server;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class NetworkMessage {

    private final long timestamp;
    private final long ttl;
    private Packet<? extends Packet> packet;
    private UUID secret;
    private SocketAddress address;
    private long sequenceNumber;

    public NetworkMessage(Packet<?> packet, UUID secret) {
        this(packet);
        this.secret = secret;
    }

    public NetworkMessage(Packet<?> packet) {
        this();
        this.packet = packet;
    }

    private NetworkMessage() {
        this.timestamp = System.currentTimeMillis();
        this.ttl = 2000L;
    }

    @Nonnull
    public Packet<? extends Packet> getPacket() {
        return packet;
    }

    public UUID getSecret() {
        return secret;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTTL() {
        return ttl;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    private static final Map<Byte, Class<? extends Packet>> packetRegistry;

    static {
        packetRegistry = new HashMap<>();
        packetRegistry.put((byte) 0, MicPacket.class);
        packetRegistry.put((byte) 1, SoundPacket.class);
        packetRegistry.put((byte) 2, AuthenticatePacket.class);
        packetRegistry.put((byte) 3, AuthenticateAckPacket.class);
        packetRegistry.put((byte) 4, PingPacket.class);
        packetRegistry.put((byte) 5, KeepAlivePacket.class);
    }

    public static NetworkMessage readPacket(DatagramSocket socket) throws IllegalAccessException, InstantiationException, IOException {
        // 4096 is the maximum packet size a packet can have with 44100 hz
        DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
        socket.receive(packet);
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream buffer = new DataInputStream(bais);
        long sequenceNumber = buffer.readLong();
        byte packetType = buffer.readByte();
        Class<? extends Packet> packetClass = packetRegistry.get(packetType);
        if (packetClass == null) {
            throw new InstantiationException("Could not find packet with ID " + packetType);
        }
        Packet<? extends Packet<?>> p = packetClass.newInstance();

        NetworkMessage message = new NetworkMessage();
        message.sequenceNumber = sequenceNumber;
        if (buffer.readBoolean()) {
            message.secret = NetUtil.readUUID(buffer);
        }
        message.address = packet.getSocketAddress();
        message.packet = p.fromBytes(buffer);

        return message;
    }

    public UUID getSender(Server server) {
        return server.getConnections().values().stream().filter(connection -> connection.getAddress().equals(address)).map(ClientConnection::getPlayerUUID).findAny().orElse(null);
    }

    private static byte getPacketType(Packet<? extends Packet> packet) {
        for (Map.Entry<Byte, Class<? extends Packet>> entry : packetRegistry.entrySet()) {
            if (packet.getClass().equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    public byte[] write(long sequenceNumber) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream buffer = new DataOutputStream(baos);

        buffer.writeLong(sequenceNumber);
        byte type = getPacketType(packet);

        if (type < 0) {
            throw new IllegalArgumentException("Packet type not found");
        }

        buffer.writeByte(type);
        buffer.writeBoolean(secret != null);
        if (secret != null) {
            NetUtil.writeUUID(buffer, secret);
        }
        packet.toBytes(buffer);

        return baos.toByteArray();
    }

}
