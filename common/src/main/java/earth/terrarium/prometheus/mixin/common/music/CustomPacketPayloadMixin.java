package earth.terrarium.prometheus.mixin.common.music;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.prometheus.common.handlers.heading.MusicSongPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(targets = "net.minecraft.network.protocol.common.custom/CustomPacketPayload$1")
public class CustomPacketPayloadMixin<B extends FriendlyByteBuf> {

    @WrapOperation(
            method = "decode(Lnet/minecraft/network/FriendlyByteBuf;)Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/codec/StreamCodec;decode(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private Object decode(
            @Coerce StreamCodec<B, CustomPacketPayload> instance,
            Object o,
            Operation<Object> original,
            @Local(ordinal = 0) ResourceLocation id
    ) {
        if (MusicSongPacketPayload.ID.equals(id)) {
            return new MusicSongPacketPayload((FriendlyByteBuf) o);
        }
        return original.call(instance, o);
    }
}
