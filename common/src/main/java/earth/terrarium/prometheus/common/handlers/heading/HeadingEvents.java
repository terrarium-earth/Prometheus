package earth.terrarium.prometheus.common.handlers.heading;

import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.client.ClientboundUpdateHeadingPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;


public class HeadingEvents {

    public static boolean onCustomPacketReceived(ServerboundCustomPayloadPacket packet, ServerPlayer player) {
        if (packet.payload() instanceof MusicSongPacketPayload payload) {
            String song = payload.song();
            if (player instanceof HeadingEntityHook hook) {
                final Heading heading = hook.prometheus$getHeading();
                if (heading == Heading.MUSIC) {
                    hook.prometheus$setHeadingText(heading.getTranslation(
                        Component.literal((song.length() <= 20 ? song : song.substring(0, 20) + "..."))
                            .withStyle(ChatFormatting.BLUE)
                    ));
                    sendToOnlinePlayers(player.getServer(), player, heading, hook.prometheus$getHeadingText());
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
                player.sendSystemMessage(CommonUtils.serverTranslatable("prometheus.heading.join", heading.getDisplayName()));
                sendToOnlinePlayers(player.getServer(), player, heading, heading.getDisplayName());
            }
            sendAllHeadings(player);
        }
    }

    public static void sendToOnlinePlayers(MinecraftServer server, Player player, Heading heading, Component text) {
        if (server == null) return;
        List<Player> players = new ArrayList<>();
        for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
            if (NetworkHandler.CHANNEL.canSendToPlayer(serverPlayer, ClientboundUpdateHeadingPacket.TYPE)) {
                players.add(serverPlayer);
            }
        }
        NetworkHandler.CHANNEL.sendToPlayers(new ClientboundUpdateHeadingPacket(List.of(new HeadingData(player.getUUID(), heading, text))), players);
    }

    public static void sendAllHeadings(ServerPlayer player) {
        if (player.getServer() == null) return;
        if (!NetworkHandler.CHANNEL.canSendToPlayer(player, ClientboundUpdateHeadingPacket.TYPE)) return;
        List<HeadingData> headings = new ArrayList<>();
        player.getServer().getPlayerList().getPlayers().forEach(p -> {
            if (p instanceof HeadingEntityHook hook) {
                headings.add(new HeadingData(p.getUUID(), hook.prometheus$getHeading(), hook.prometheus$getHeadingText()));
            }
        });
        NetworkHandler.CHANNEL.sendToPlayer(new ClientboundUpdateHeadingPacket(headings), player);
    }
}
