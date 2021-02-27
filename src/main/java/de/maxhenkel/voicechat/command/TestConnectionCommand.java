package de.maxhenkel.voicechat.command;

public class TestConnectionCommand {

    // public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
    // LiteralArgumentBuilder<ServerCommandSource> literalBuilder = CommandManager.literal("voicechat").requires((commandSource) -> commandSource.hasPermissionLevel(2));
    //
    // literalBuilder.then(CommandManager.literal("test").then(CommandManager.argument("target", EntityArgumentType.player()).executes((commandSource) -> {
    // ServerPlayerEntity player = EntityArgumentType.getPlayer(commandSource, "target");
    // Server server = Voicechat.SERVER.getServer();
    // if (server == null) {
    // commandSource.getSource().sendFeedback(new TranslatableText("message.voicechat.voice_chat_unavailable"), true);
    // return 1;
    // }
    // ClientConnection clientConnection = server.getConnections().get(player.getUuid());
    // if (clientConnection == null) {
    // commandSource.getSource().sendFeedback(new TranslatableText("message.voicechat.client_not_connected"), true);
    // return 1;
    // }
    // try {
    // commandSource.getSource().sendFeedback(new TranslatableText("message.voicechat.sending_packet"), true);
    // long timestamp = System.currentTimeMillis();
    // server.getPingManager().sendPing(clientConnection, 5000, new PingManager.PingListener() {
    // @Override
    // public void onPong(PingPacket packet) {
    // commandSource.getSource().sendFeedback(new TranslatableText("message.voicechat.packet_received", (System.currentTimeMillis() - timestamp)), true);
    // }
    //
    // @Override
    // public void onTimeout() {
    // commandSource.getSource().sendFeedback(new TranslatableText("message.voicechat.packet_timed_out"), true);
    // }
    // });
    // commandSource.getSource().sendFeedback(new TranslatableText("message.voicechat.packet_sent_waiting"), true);
    // } catch (IOException e) {
    // commandSource.getSource().sendFeedback(new TranslatableText("message.voicechat.failed_to_send_packet", e.getMessage()), true);
    // e.printStackTrace();
    // return 1;
    // }
    // return 1;
    // })));
    //
    // dispatcher.register(literalBuilder);
    // }

}
