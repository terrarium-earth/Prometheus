package earth.terrarium.prometheus.common.handlers.tpa;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaHandler {

    private static final Component CANT_TP_TO_SELF = Component.translatable("prometheus.tpa.error.self_request");
    private static final Component EXPIRED = Component.translatable("prometheus.tpa.error.expired_request");
    private static final Component INVALID = Component.translatable("prometheus.tpa.error.invalid_request");
    private static final Component DENIED = Component.translatable("prometheus.tpa.error.request_denied");
    private static final Component OFFLINE = Component.translatable("prometheus.tpa.error.sender_offline");
    private static final Component TELEPORTING = Component.translatable("prometheus.tpa.teleporting");
    private static final Component REQUEST = Component.translatable("prometheus.tpa.request");

    private static final long REQUEST_EXPIRY = 30000;

    public static final Map<UUID, TpaRequest> REQUESTS = new HashMap<>();

    public static void sendRequest(Player player, Player target, TpaRequest.Direction direction) {
        if (player.getUUID().equals(target.getUUID())) {
            player.sendSystemMessage(CANT_TP_TO_SELF);
            return;
        }
        TpaRequest request = new TpaRequest(player.getUUID(), target.getUUID(), direction);
        REQUESTS.put(player.getUUID(), request);
        target.sendSystemMessage(request.getMessage(player));
        player.sendSystemMessage(REQUEST);
    }

    public static TpaRequest getRequest(Player player, UUID id) {
        TpaRequest request = REQUESTS.get(id);
        if (request != null && request.receiver().equals(player.getUUID())) {
            if (System.currentTimeMillis() - request.time() > REQUEST_EXPIRY) {
                REQUESTS.remove(id);
                player.sendSystemMessage(EXPIRED);
                return null;
            }
            return request;
        }
        player.sendSystemMessage(INVALID);
        return null;
    }

    public static void denyRequest(ServerPlayer player, UUID id) {
        TpaRequest request = getRequest(player, id);
        if (request != null) {
            REQUESTS.remove(id);
            player.sendSystemMessage(DENIED);
        }
    }

    public static void acceptRequest(ServerPlayer player, UUID id) {
        TpaRequest request = getRequest(player, id);
        if (request != null) {
            REQUESTS.remove(id);
            ServerPlayer sender = player.server.getPlayerList().getPlayer(request.sender());
            if (sender == null) {
                player.sendSystemMessage(OFFLINE);
                return;
            }
            final ServerPlayer to = request.direction() == TpaRequest.Direction.TO ? player : sender;
            final ServerPlayer from = request.direction() == TpaRequest.Direction.FROM ? player : sender;

            from.sendSystemMessage(TELEPORTING, true);

            from.teleportTo(to.getLevel(), to.getX(), to.getY(), to.getZ(), to.getYRot(), to.getXRot());
        }
    }
}
