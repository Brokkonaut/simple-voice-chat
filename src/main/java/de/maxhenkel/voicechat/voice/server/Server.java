package de.maxhenkel.voicechat.voice.server;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.voice.common.AuthenticateAckPacket;
import de.maxhenkel.voicechat.voice.common.AuthenticatePacket;
import de.maxhenkel.voicechat.voice.common.KeepAlivePacket;
import de.maxhenkel.voicechat.voice.common.MicPacket;
import de.maxhenkel.voicechat.voice.common.NetworkMessage;
import de.maxhenkel.voicechat.voice.common.Packet;
import de.maxhenkel.voicechat.voice.common.PingPacket;
import de.maxhenkel.voicechat.voice.common.SoundPacket;
import de.maxhenkel.voicechat.voice.common.Utils;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Server extends Thread {

    private Map<UUID, ClientConnection> connections;
    private Map<UUID, UUID> secrets;
    private int port;
    private Voicechat plugin;
    private org.bukkit.Server server;
    private DatagramSocket socket;
    private ProcessThread processThread;
    private List<NetworkMessage> packetQueue;
    private PingManager pingManager;

    public Server(int port, Voicechat plugin) {
        this.plugin = plugin;
        this.port = port;
        this.server = plugin.getServer();
        connections = new HashMap<>();
        secrets = new HashMap<>();
        packetQueue = new ArrayList<>();
        pingManager = new PingManager(this);
        setDaemon(true);
        setName("VoiceChatServerThread");
        processThread = new ProcessThread();
        processThread.start();
    }

    @Override
    public void run() {
        try {
            InetAddress address = null;
            String addr = Voicechat.SERVER_CONFIG.voiceChatBindAddress.get();
            try {
                if (!addr.isEmpty()) {
                    address = InetAddress.getByName(addr);
                }
            } catch (Exception e) {
                Voicechat.LOGGER.severe("Failed to parse bind IP address '" + addr + "'");
                Voicechat.LOGGER.info("Binding to default IP address");
                e.printStackTrace();
            }
            try {
                socket = new DatagramSocket(port, address);
                socket.setTrafficClass(0x04); // IPTOS_RELIABILITY
            } catch (BindException e) {
                Voicechat.LOGGER.severe("Failed to bind to address '" + addr + "'");
                e.printStackTrace();
                System.exit(1);
                return;
            }
            Voicechat.LOGGER.info("Server started at port " + port);

            while (!socket.isClosed()) {
                try {
                    NetworkMessage message = NetworkMessage.readPacket(socket);
                    packetQueue.add(message);
                } catch (Exception e) {
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public UUID getSecret(UUID playerUUID) {
        if (secrets.containsKey(playerUUID)) {
            return secrets.get(playerUUID);
        } else {
            UUID secret = UUID.randomUUID();
            secrets.put(playerUUID, secret);
            return secret;
        }
    }

    public void disconnectClient(UUID playerUUID) {
        connections.remove(playerUUID);
        secrets.remove(playerUUID);
    }

    public void close() {
        socket.close();
        processThread.close();
    }

    private class ProcessThread extends Thread {
        private boolean running;

        public ProcessThread() {
            this.running = true;
            setDaemon(true);
            setName("VoiceChatPacketProcessingThread");
        }

        @Override
        public void run() {
            while (running) {
                try {
                    pingManager.checkTimeouts();
                    keepAlive();
                    if (packetQueue.isEmpty()) {
                        Utils.sleep(10);
                        continue;
                    }

                    NetworkMessage message = packetQueue.get(0);
                    packetQueue.remove(message);
                    if (System.currentTimeMillis() - message.getTimestamp() > message.getTTL()) {
                        continue;
                    }

                    if (message.getPacket() instanceof AuthenticatePacket) {
                        AuthenticatePacket packet = (AuthenticatePacket) message.getPacket();
                        UUID secret = secrets.get(packet.getPlayerUUID());
                        if (secret != null && secret.equals(packet.getSecret())) {
                            ClientConnection connection;
                            if (!connections.containsKey(packet.getPlayerUUID())) {
                                connection = new ClientConnection(packet.getPlayerUUID(), message.getAddress());
                                connections.put(packet.getPlayerUUID(), connection);
                                Voicechat.LOGGER.info("Successfully authenticated player " + packet.getPlayerUUID());
                            } else {
                                connection = connections.get(packet.getPlayerUUID());
                            }
                            sendPacket(new AuthenticateAckPacket(), connection);
                        }
                    }

                    UUID playerUUID = message.getSender(Server.this);
                    if (playerUUID == null) {
                        continue;
                    }

                    if (!isPacketAuthorized(message, playerUUID)) {
                        continue;
                    }

                    ClientConnection conn = connections.get(playerUUID);
                    if (conn == null || message.getSequenceNumber() <= conn.getLastClientSequenceNumber()) {
                        continue;
                    }
                    conn.setLastClientSequenceNumber(message.getSequenceNumber());

                    if (message.getPacket() instanceof MicPacket) {
                        MicPacket packet = (MicPacket) message.getPacket();
                        Player player = server.getPlayer(playerUUID);
                        if (player == null) {
                            continue;
                        }
                        double distance = Voicechat.SERVER_CONFIG.voiceChatDistance.get();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                List<ClientConnection> closeConnections = player.getWorld().getNearbyEntitiesByType(Player.class, player.getLocation(), distance, playerEntity -> !playerEntity.getUniqueId().equals(player.getUniqueId()))
                                        .stream()
                                        .map(playerEntity -> connections.get(playerEntity.getUniqueId()))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());
                                NetworkMessage soundMessage = new NetworkMessage(new SoundPacket(playerUUID, packet.getData()));
                                for (ClientConnection clientConnection : closeConnections) {
                                    if (!clientConnection.getPlayerUUID().equals(playerUUID)) {
                                        try {
                                            clientConnection.send(socket, soundMessage);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }.runTask(plugin);
                    } else if (message.getPacket() instanceof PingPacket) {
                        pingManager.onPongPacket((PingPacket) message.getPacket());
                    } else if (message.getPacket() instanceof KeepAlivePacket) {
                        conn.setLastKeepAliveResponse(System.currentTimeMillis());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void close() {
            running = false;
        }
    }

    private void keepAlive() throws IOException {
        long timestamp = System.currentTimeMillis();
        KeepAlivePacket keepAlive = new KeepAlivePacket();
        for (ClientConnection connection : connections.values()) {
            if (timestamp - connection.getLastKeepAliveResponse() >= Voicechat.SERVER_CONFIG.keepAlive.get() * 10L) {
                disconnectClient(connection.getPlayerUUID());
                Voicechat.LOGGER.info("Player " + connection.getPlayerUUID() + " timed out");
                Player player = server.getPlayer(connection.getPlayerUUID());
                if (player != null) {
                    Voicechat.LOGGER.info("Reconnecting player " + player.getDisplayName());
                    Voicechat.SERVER.initializePlayerConnection(player);
                }
            } else if (timestamp - connection.getLastKeepAlive() >= Voicechat.SERVER_CONFIG.keepAlive.get()) {
                connection.setLastKeepAlive(timestamp);
                sendPacket(keepAlive, connection);
            }
        }
    }

    private boolean isPacketAuthorized(NetworkMessage message, UUID sender) {
        UUID secret = secrets.get(sender);
        return secret != null && secret.equals(message.getSecret());
    }

    public Map<UUID, ClientConnection> getConnections() {
        return connections;
    }

    public Map<UUID, UUID> getSecrets() {
        return secrets;
    }

    public void sendPacket(Packet<?> packet, ClientConnection connection) throws IOException {
        connection.send(socket, new NetworkMessage(packet));
    }

    public PingManager getPingManager() {
        return pingManager;
    }
}
