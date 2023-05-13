package earth.terrarium.prometheus.client.handlers;

import earth.terrarium.prometheus.client.utils.SystemNotificationUtils;
import earth.terrarium.prometheus.common.registries.ModSounds;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.UUID;
import java.util.function.IntFunction;

public class NotificationHandler {

    public static void onChatMessage(Component message, @Nullable UUID sender, ChatType.Bound params) {
        if (Minecraft.getInstance().getConnection() == null) return;
        if (sender == null) return;
        if (Minecraft.getInstance().player == null) return;
        if (Minecraft.getInstance().player.getGameProfile().getId().equals(sender)) return;
        if (Minecraft.getInstance().getPlayerSocialManager().shouldHideMessageFrom(sender)) return;
        String name = Minecraft.getInstance().player.getGameProfile().getName();
        String senderName = Optionull.mapOrDefault(Minecraft.getInstance().getConnection().getPlayerInfo(sender), info -> info.getProfile().getName(), "");
        Type type = getType(params, Minecraft.getInstance().getConnection().registryAccess());
        String text = message.getString().toLowerCase(Locale.ROOT);

        boolean shouldPing = text.contains("@" + name.toLowerCase(Locale.ROOT)) || type == Type.PRIVATE;

        if (!ClientOptionHandler.showNotifications.get() || !shouldPing) return;

        if (Minecraft.getInstance().isWindowActive()) {
            switch (ClientOptionHandler.notificationSound.get()) {
                case PING1 ->
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.PING_1.get(), 1.0F));
                case PING2 ->
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.PING_2.get(), 1.0F));
                case PING3 ->
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.PING_3.get(), 1.0F));
            }
            return;
        }

        switch (type) {
            case PRIVATE -> SystemNotificationUtils.sendNotification(
                "Private Message Received",
                "You received a private message from " + senderName + "!"
            );
            case TEAM -> SystemNotificationUtils.sendNotification(
                "You've been mentioned in a team message",
                "You were mentioned by " + senderName + "!"
            );
            default -> SystemNotificationUtils.sendNotification(
                "You've been mentioned in a message",
                "You were mentioned by " + senderName + "!"
            );
        }

    }

    private static Type getType(ChatType.Bound type, RegistryAccess access) {
        var chatType = access.registry(Registries.CHAT_TYPE).map(reg -> reg.getKey(type.chatType())).orElse(null);
        if (chatType != null) {
            if (chatType.equals(ChatType.MSG_COMMAND_INCOMING.location())) return Type.PRIVATE;
            if (chatType.equals(ChatType.TEAM_MSG_COMMAND_INCOMING.location())) return Type.TEAM;
            if (chatType.equals(ChatType.CHAT.location())) return Type.PUBLIC;
        }
        return Type.UNKNOWN;
    }

    private enum Type {
        PUBLIC,
        PRIVATE,
        TEAM,
        UNKNOWN
    }

    public enum PingSound implements OptionEnum {
        NONE,
        PING1,
        PING2,
        PING3;

        private static final IntFunction<PingSound> BY_ID = ByIdMap.continuous(PingSound::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

        public static PingSound byId(int id) {
            return BY_ID.apply(id);
        }

        @Override
        public int getId() {
            return ordinal();
        }

        @Override
        public @NotNull String getKey() {
            return name().charAt(0) + name().substring(1).toLowerCase(Locale.ROOT);
        }
    }
}
