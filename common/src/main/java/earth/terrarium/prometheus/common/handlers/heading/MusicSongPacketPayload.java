package earth.terrarium.prometheus.common.handlers.heading;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public record MusicSongPacketPayload(String song) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.parse("music:song");

    public MusicSongPacketPayload(FriendlyByteBuf buffer) {
        this(new String(buffer.readByteArray(32767), StandardCharsets.UTF_8));
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return new Type<>(ID);
    }
}
