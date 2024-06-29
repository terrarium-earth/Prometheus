package earth.terrarium.prometheus.common.handlers.heading;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record HeadingData(UUID id, Heading heading, @Nullable Component text) {

    public static final ByteCodec<HeadingData> BYTE_CODEC = ObjectByteCodec.create(
            ByteCodec.UUID.fieldOf(HeadingData::id),
            Heading.BYTE_CODEC.fieldOf(HeadingData::heading),
            ExtraByteCodecs.COMPONENT.nullableFieldOf(HeadingData::text),
            HeadingData::new
    );
}
