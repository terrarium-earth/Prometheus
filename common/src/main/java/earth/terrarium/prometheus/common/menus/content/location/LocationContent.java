package earth.terrarium.prometheus.common.menus.content.location;

import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public record LocationContent(LocationType type, int max, List<Location> locations) {

    public boolean canModify() {
        return max != -1;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(type);
        buf.writeVarInt(max);
        buf.writeCollection(locations, (buf1, location) -> {
            buf.writeUtf(location.name());
            buf.writeGlobalPos(location.pos());
        });
    }

    public static LocationContent read(FriendlyByteBuf buf) {
        LocationType type = buf.readEnum(LocationType.class);
        int max = buf.readVarInt();
        List<Location> locations = buf.readList(buf1 -> new Location(buf1.readUtf(), buf1.readGlobalPos()));
        return new LocationContent(type, max, locations);
    }
}
