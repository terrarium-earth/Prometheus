package earth.terrarium.prometheus.common.handlers.heading;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record HeadingData(UUID id, Heading heading, @Nullable Component text) {

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.id());
        buf.writeEnum(this.heading());
        buf.writeBoolean(this.text() != null);
        if (this.text() != null) buf.writeComponent(this.text());
    }

    public static HeadingData read(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        Heading heading = buf.readEnum(Heading.class);
        Component text = buf.readBoolean() ? buf.readComponent() : null;
        return new HeadingData(id, heading, text);
    }
}
