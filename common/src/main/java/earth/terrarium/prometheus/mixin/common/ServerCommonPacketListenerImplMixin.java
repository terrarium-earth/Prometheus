package earth.terrarium.prometheus.mixin.common;

import earth.terrarium.prometheus.common.handlers.heading.HeadingEvents;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    public void onCustomPacket(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket, CallbackInfo ci) {
        Object thisObj = this;
        //noinspection ConstantValue
        if (thisObj instanceof ServerGamePacketListenerImpl listener) {
            ServerPlayer player = listener.getPlayer();
            if (HeadingEvents.onCustomPacketReceived(serverboundCustomPayloadPacket, player)) {
                ci.cancel();
            }
        }
    }
}