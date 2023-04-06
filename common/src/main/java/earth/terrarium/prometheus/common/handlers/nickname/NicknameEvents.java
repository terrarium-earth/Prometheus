package earth.terrarium.prometheus.common.handlers.nickname;

import net.minecraft.server.level.ServerPlayer;

public class NicknameEvents {

    public static void onJoin(ServerPlayer player) {
        if (player instanceof NickedEntityHook hook) {
            hook.prometheus$setNickname(NicknameHandler.get(player));
        }
    }
}
