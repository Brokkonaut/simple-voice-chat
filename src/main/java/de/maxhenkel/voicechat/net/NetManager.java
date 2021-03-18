package de.maxhenkel.voicechat.net;

import de.maxhenkel.voicechat.Voicechat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class NetManager {

    public static <T extends Packet<T>> void registerServerReceiver(Voicechat plugin, Class<T> packetType, ServerReceiver<T> packetReceiver) {
        try {
            T dummyPacket = packetType.getConstructor().newInstance();
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, dummyPacket.getID(), (channel, player, buf) -> {
                try {
                    T packet = packetType.getConstructor().newInstance();
                    packet.fromBytes(new DataInputStream(new ByteArrayInputStream(buf)));
                    packetReceiver.onPacket(plugin.getServer(), player, packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void sendToClient(Voicechat plugin, Player player, Packet<?> packet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream buffer = new DataOutputStream(baos);
        try {
            packet.toBytes(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, packet.getID(), baos.toByteArray());
    }

    public static interface ServerReceiver<T extends Packet<T>> {
        void onPacket(Server server, Player player, T packet);
    }
}
