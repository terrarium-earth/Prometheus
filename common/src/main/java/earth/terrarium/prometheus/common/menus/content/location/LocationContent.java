package earth.terrarium.prometheus.common.menus.content.location;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;

import java.util.List;

public record LocationContent(LocationType type, int max, List<Location> locations) {

    public static final ByteCodec<LocationContent> BYTE_CODEC = ObjectByteCodec.create(
        LocationType.BYTE_CODEC.fieldOf(LocationContent::type),
        ByteCodec.VAR_INT.fieldOf(LocationContent::max),
        Location.BYTE_CODEC.listOf().fieldOf(LocationContent::locations),
        LocationContent::new
    );

    public boolean canModify() {
        return max != -1;
    }
}
