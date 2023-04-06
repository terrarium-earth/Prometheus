package earth.terrarium.prometheus.common.handlers.heading;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.StandardCharsets;


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
            }
        }
    }
}
