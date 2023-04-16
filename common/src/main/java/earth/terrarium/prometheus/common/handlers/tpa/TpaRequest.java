package earth.terrarium.prometheus.common.handlers.tpa;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record TpaRequest(long time, int expires, UUID sender, UUID receiver, Direction direction) {

    public TpaRequest(UUID sender, UUID receiver, int expires, Direction direction) {
        this(System.currentTimeMillis(), expires, sender, receiver, direction);
    }

    public Component getMessage(Player sender) {
        return Component.translatable(
            "prometheus.tpa.request." + direction().name().toLowerCase(),
            sender.getDisplayName(),
            Component.empty().append(getAcceptComponent()).append(" ").append(getRejectComponent())
        );
    }

    private Component getAcceptComponent() {
        return ComponentUtils.wrapInSquareBrackets(Component.translatable("mco.invites.button.accept"))
            .withStyle(Style.EMPTY
                .applyFormat(ChatFormatting.GREEN)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept " + sender()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("mco.invites.button.accept")))
            );
    }

    private Component getRejectComponent() {
        return ComponentUtils.wrapInSquareBrackets(Component.translatable("mco.invites.button.reject"))
            .withStyle(Style.EMPTY
                .applyFormat(ChatFormatting.RED)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny " + sender()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("mco.invites.button.reject")))
            );
    }

    public enum Direction {
        TO,
        FROM
    }
}
