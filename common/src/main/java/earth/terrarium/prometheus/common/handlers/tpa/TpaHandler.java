package earth.terrarium.prometheus.common.handlers.tpa;

import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaHandler {

    public static final Map<UUID, TpaRequest> REQUESTS = new HashMap<>();

    public static void sendRequest(Player player, Player target, TpaRequest.Direction direction) {
        if (player.getUUID().equals(target.getUUID())) {
            player.sendSystemMessage(ConstantComponents.CANT_TP_TO_SELF);
            return;
        }
        TeleportOptions options = RoleApi.API.getNonNullOption(player, TeleportOptions.SERIALIZER);
        TpaRequest request = new TpaRequest(player.getUUID(), target.getUUID(), options.expire(), direction);
        REQUESTS.put(player.getUUID(), request);
        target.sendSystemMessage(request.getMessage(player));
        player.sendSystemMessage(ConstantComponents.REQUEST);
    }

    public static TpaRequest getRequest(Player player, UUID id) {
        TpaRequest request = REQUESTS.get(id);
        if (request != null && request.receiver().equals(player.getUUID())) {
            if (System.currentTimeMillis() - request.time() > request.expires()) {
                REQUESTS.remove(id);
                player.sendSystemMessage(ConstantComponents.EXPIRED);
                return null;
            }
            return request;
        }
        player.sendSystemMessage(ConstantComponents.INVALID);
        return null;
    }

    public static void denyRequest(ServerPlayer player, UUID id) {
        TpaRequest request = getRequest(player, id);
        if (request != null) {
            REQUESTS.remove(id);
            player.sendSystemMessage(ConstantComponents.DENIED);
        }
    }

    public static void acceptRequest(ServerPlayer player, UUID id) {
        TpaRequest request = getRequest(player, id);
        if (request != null) {
            REQUESTS.remove(id);
            ServerPlayer sender = player.server.getPlayerList().getPlayer(request.sender());
            if (sender == null) {
                player.sendSystemMessage(ConstantComponents.OFFLINE);
                return;
            }
            final ServerPlayer to = request.direction() == TpaRequest.Direction.TO ? player : sender;
            final ServerPlayer from = request.direction() == TpaRequest.Direction.FROM ? player : sender;

            from.sendSystemMessage(ConstantComponents.TELEPORTING, true);

            from.teleportTo(to.serverLevel(), to.getX(), to.getY(), to.getZ(), to.getYRot(), to.getXRot());
        }
    }
}
