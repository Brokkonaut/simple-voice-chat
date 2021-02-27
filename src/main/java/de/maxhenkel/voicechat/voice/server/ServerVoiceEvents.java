package de.maxhenkel.voicechat.voice.server;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.net.InitPacket;
import de.maxhenkel.voicechat.net.Packets;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerVoiceEvents implements Listener {

    private Voicechat plugin;
    private Server server;

    public ServerVoiceEvents(Voicechat plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        serverStarting(plugin.getServer());
    }

    public void serverStarting(org.bukkit.Server mcServer) {
        if (server != null) {
            server.close();
            server = null;
        }
        try {
            server = new Server(Voicechat.SERVER_CONFIG.voiceChatPort.get(), mcServer);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        initializePlayerConnection(e.getPlayer());
    }

    public void initializePlayerConnection(Player player) {
        if (server == null) {
            return;
        }
        UUID secret = server.getSecret(player.getUniqueId());
        InitPacket packet = new InitPacket(secret, Voicechat.SERVER_CONFIG.voiceChatPort.get(), Voicechat.SERVER_CONFIG.voiceChatSampleRate.get(), Voicechat.SERVER_CONFIG.voiceChatMtuSize.get(), Voicechat.SERVER_CONFIG.voiceChatDistance.get(), Voicechat.SERVER_CONFIG.voiceChatFadeDistance.get(),
                Voicechat.SERVER_CONFIG.keepAlive.get());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            packet.toBytes(new DataOutputStream(baos));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create player list packet", e);
        }
        player.sendPluginMessage(plugin, Packets.SECRET, baos.toByteArray());
        Voicechat.LOGGER.info("Sent secret to " + player.getDisplayName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (server == null) {
            return;
        }
        Player player = e.getPlayer();

        server.disconnectClient(player.getUniqueId());
        Voicechat.LOGGER.info("Disconnecting client " + player.getDisplayName());
    }

    @Nullable
    public Server getServer() {
        return server;
    }
}
