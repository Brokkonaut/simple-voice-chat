package de.maxhenkel.voicechat.voice.server;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.net.NetManager;
import de.maxhenkel.voicechat.net.PlayerStatePacket;
import de.maxhenkel.voicechat.net.PlayerStatesPacket;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerStateManager {

    private Voicechat plugin;
    private Map<UUID, PlayerState> states;

    public PlayerStateManager(Voicechat plugin) {
        this.plugin = plugin;
        states = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new StateListener(), plugin);

        NetManager.registerServerReceiver(this.plugin, PlayerStatePacket.class, (plugin1, player, packet) -> {
            states.put(player.getUniqueId(), packet.getPlayerState());
            broadcastState(plugin.getServer(), player.getUniqueId(), packet.getPlayerState());
        });
    }

    private class StateListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent e) {
            notifyPlayer(e.getPlayer());
        }

        @EventHandler
        public void onPlayerJoin(PlayerQuitEvent e) {
            removePlayer(e.getPlayer());
        }
    }

    private void broadcastState(org.bukkit.Server server, UUID uuid, PlayerState state) {
        PlayerStatePacket packet = new PlayerStatePacket(uuid, state);
        server.getOnlinePlayers().forEach(p -> {
            if (!p.getUniqueId().equals(uuid)) {
                NetManager.sendToClient(plugin, p, packet);
            }
        });
    }

    private void notifyPlayer(Player player) {
        PlayerStatesPacket packet = new PlayerStatesPacket(states);
        NetManager.sendToClient(plugin, player, packet);
        broadcastState(player.getServer(), player.getUniqueId(), new PlayerState(false, true));
    }

    private void removePlayer(Player player) {
        states.remove(player.getUniqueId());
        broadcastState(player.getServer(), player.getUniqueId(), new PlayerState(true, true));
    }

}
