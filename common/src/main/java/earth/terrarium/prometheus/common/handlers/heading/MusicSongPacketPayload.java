package earth.terrarium.prometheus.common.handlers.heading;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public record MusicSongPacketPayload(String song) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation("music:song");

    public MusicSongPacketPayload(FriendlyByteBuf buffer) {
        this(new String(buffer.readByteArray(32767), StandardCharsets.UTF_8));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeByteArray(song.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
