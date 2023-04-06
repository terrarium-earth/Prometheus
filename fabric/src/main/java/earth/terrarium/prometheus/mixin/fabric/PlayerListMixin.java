package earth.terrarium.prometheus.mixin.fabric;

import earth.terrarium.prometheus.common.handlers.heading.HeadingEvents;
import earth.terrarium.prometheus.common.handlers.nickname.NicknameEvents;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void prometheus$onPlayerLogin(Connection connection, ServerPlayer serverPlayer, CallbackInfo ci) {
        HeadingEvents.onJoin(serverPlayer);
        NicknameEvents.onJoin(serverPlayer);
    }
}
