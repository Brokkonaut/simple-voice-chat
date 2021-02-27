package de.maxhenkel.voicechat;

import de.maxhenkel.voicechat.config.ConfigBuilder;
import de.maxhenkel.voicechat.config.ServerConfig;
import de.maxhenkel.voicechat.net.Packets;
import de.maxhenkel.voicechat.net.PlayerListPacket;
import de.maxhenkel.voicechat.voice.server.ServerVoiceEvents;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;

public class Voicechat extends JavaPlugin {

    public static final String MODID = "voicechat";
    public static Logger LOGGER;
    public static ServerVoiceEvents SERVER;
    @Nullable
    public static ServerConfig SERVER_CONFIG;

    @Override
    public void onLoad() {
        LOGGER = getLogger();
    }

    @Override
    public void onEnable() {
        ConfigBuilder.create(getDataFolder().toPath().resolve("voicechat-server.properties"), builder -> SERVER_CONFIG = new ServerConfig(builder));

        // CommandRegistrationCallback.EVENT.register(TestConnectionCommand::register);

        SERVER = new ServerVoiceEvents(this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, Packets.SECRET);
        getServer().getMessenger().registerOutgoingPluginChannel(this, Packets.PLAYER_LIST);
        getServer().getMessenger().registerIncomingPluginChannel(this, Packets.REQUEST_PLAYER_LIST, (packet, player, data) -> {
            List<PlayerInfo> players = player
                    .getServer()
                    .getOnlinePlayers()
                    .stream()
                    .filter(p -> !p.getUniqueId().equals(player.getUniqueId()))
                    .map(playerEntity -> new PlayerInfo(playerEntity.getUniqueId(), TextComponent.fromLegacyText(playerEntity.getDisplayName())[0]))
                    .collect(Collectors.toList());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                new PlayerListPacket(players).toBytes(new DataOutputStream(baos));
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Could not create player list packet", e);
            }
            player.sendPluginMessage(this, Packets.PLAYER_LIST, baos.toByteArray());
        });
    }
}
