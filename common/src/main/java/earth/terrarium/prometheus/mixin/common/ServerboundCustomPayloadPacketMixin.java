package earth.terrarium.prometheus.mixin.common;

import earth.terrarium.prometheus.common.handlers.heading.MusicSongPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerboundCustomPayloadPacket.class)
public class ServerboundCustomPayloadPacketMixin {

    @Inject(method = "readPayload", at = @At("HEAD"), cancellable = true)
    private static void prometheus$readPayload(ResourceLocation id, FriendlyByteBuf buffer, CallbackInfoReturnable<CustomPacketPayload> cir) {
        if (id.equals(MusicSongPacketPayload.ID)) {
            cir.setReturnValue(new MusicSongPacketPayload(buffer));
        }
    }
}
