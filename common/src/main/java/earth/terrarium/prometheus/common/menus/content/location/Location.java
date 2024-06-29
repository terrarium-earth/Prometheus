package earth.terrarium.prometheus.common.menus.content.location;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.bytecodecs.ExtraByteCodecs;
import net.minecraft.core.GlobalPos;

public record Location(String name, GlobalPos pos) {

    public static final ByteCodec<Location> BYTE_CODEC = ObjectByteCodec.create(
        ByteCodec.STRING.fieldOf(Location::name),
        ExtraByteCodecs.GLOBAL_POS.fieldOf(Location::pos),
        Location::new
    );
}