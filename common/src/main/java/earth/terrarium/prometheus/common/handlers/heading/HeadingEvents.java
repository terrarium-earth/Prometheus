package earth.terrarium.prometheus.common.handlers.heading;

import com.mojang.datafixers.util.Pair;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.UpdateHeadingPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class HeadingEvents {

    public static boolean onCustomPacketReceived(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, ServerPlayer player) {
        if (serverboundCustomPayloadPacket.getIdentifier().toString().equals("music:song")) {
            FriendlyByteBuf data = serverboundCustomPayloadPacket.getData();
            byte[] bytes = data.readByteArray(32767);
            String song = new String(bytes, StandardCharsets.UTF_8);
            if (player instanceof HeadingEntityHook hook) {
                final Heading heading = hook.prometheus$getHeading();
                if (hook.prometheus$getHeading() == Heading.MUSIC) {
                    hook.prometheus$setHeadingText(heading.getTranslation(
                        Component.literal((song.length() <= 20 ? song : song.substring(0, 20) + "..."))
                            .withStyle(ChatFormatting.BLUE)
                    ));
                }
            }
            return true;
        }
        return false;
    }

    public static void onJoin(ServerPlayer player) {
        if (player instanceof HeadingEntityHook hook) {
            Heading heading = HeadingHandler.get(player);
            if (heading != null && heading != hook.prometheus$getHeading()) {
                hook.prometheus$setHeadingAndUpdate(heading);
                player.sendSystemMessage(Component.translatable("prometheus.heading.join", heading.getDisplayName()));
                sendToOnlinePlayers(player.getServer(), player, heading);
            }
            sendAllHeadings(player);
        }
    }

    public static void sendToOnlinePlayers(MinecraftServer server, Player player, Heading heading) {
        if (server == null) return;
        List<Player> players = new ArrayList<>();
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            if (NetworkHandler.CHANNEL.canSendPlayerPackets(serverPlayer)) {
                players.add(serverPlayer);
            }
        }
        NetworkHandler.CHANNEL.sendToPlayers(new UpdateHeadingPacket(List.of(Pair.of(player.getUUID(), heading))), players);
    }

    public static void sendAllHeadings(ServerPlayer player) {
        if (player.getServer() == null) return;
        List<Pair<UUID, Heading>> headings = new ArrayList<>();
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            if (p instanceof HeadingEntityHook hook) {
                headings.add(Pair.of(p.getUUID(), hook.prometheus$getHeading()));
            }
        });
        NetworkHandler.CHANNEL.sendToPlayer(new UpdateHeadingPacket(headings), player);
    }
}
