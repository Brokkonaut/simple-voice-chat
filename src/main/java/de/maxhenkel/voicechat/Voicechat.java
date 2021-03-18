package de.maxhenkel.voicechat;

import de.maxhenkel.voicechat.config.ConfigBuilder;
import de.maxhenkel.voicechat.config.ServerConfig;
import de.maxhenkel.voicechat.net.InitPacket;
import de.maxhenkel.voicechat.net.PlayerListPacket;
import de.maxhenkel.voicechat.net.PlayerStatePacket;
import de.maxhenkel.voicechat.net.PlayerStatesPacket;
import de.maxhenkel.voicechat.net.RequestPlayerListPacket;
import de.maxhenkel.voicechat.voice.server.ServerVoiceEvents;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
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

    public static final String INIT = Voicechat.MODID + ":" + "init";
    public static int COMPATIBILITY_VERSION = -1;

    @Override
    public void onLoad() {
        LOGGER = getLogger();
    }

    public void onInitialize() {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("compatibility.properties");
            Properties props = new Properties();
            props.load(in);
            COMPATIBILITY_VERSION = Integer.parseInt(props.getProperty("compatibility_version"));
            LOGGER.info("Compatibility version " + COMPATIBILITY_VERSION);
        } catch (Exception e) {
            LOGGER.severe("Failed to read compatibility version");
        }
    }

    @Override
    public void onEnable() {
        ConfigBuilder.create(getDataFolder().toPath().resolve("voicechat-server.properties"), builder -> SERVER_CONFIG = new ServerConfig(builder));
        // has to be sent a Login Plugin Request packet
        // ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
        // PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        // buffer.writeInt(COMPATIBILITY_VERSION);
        // sender.sendPacket(INIT, buffer);
        // });

        // CommandRegistrationCallback.EVENT.register(TestConnectionCommand::register);

        SERVER = new ServerVoiceEvents(this);

        getServer().getMessenger().registerOutgoingPluginChannel(this, INIT);
        getServer().getMessenger().registerOutgoingPluginChannel(this, InitPacket.SECRET);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PlayerListPacket.PLAYER_LIST);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PlayerStatesPacket.PLAYER_STATES);
        getServer().getMessenger().registerOutgoingPluginChannel(this, PlayerStatePacket.PLAYER_STATE);
        // has to be sent a Login Plugin Response packet
        // getServer().getMessenger().registerIncomingPluginChannel(this, INIT, (packet, player, data) -> {
        // DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        // // if (!understood) {
        // // // Let vanilla clients pass, but not incompatible voice chat clients
        // // return;
        // // }
        //
        // int clientCompatibilityVersion = dis.readInt();
        //
        // if (clientCompatibilityVersion != Voicechat.COMPATIBILITY_VERSION) {
        // Voicechat.LOGGER.warning("Client " + player.getName() + " has incompatible voice chat version (server=" + Voicechat.COMPATIBILITY_VERSION + ", client=" + clientCompatibilityVersion + ")");
        // player.kickPlayer("Your voice chat version is not compatible with the servers version");
        // }
        // });

        getServer().getMessenger().registerIncomingPluginChannel(this, RequestPlayerListPacket.REQUEST_PLAYER_LIST, (packet, player, data) -> {
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
            player.sendPluginMessage(this, PlayerListPacket.PLAYER_LIST, baos.toByteArray());
        });
    }
}
