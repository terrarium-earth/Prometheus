package earth.terrarium.prometheus.common.menus.location;

import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;

public record Location(String name, GlobalPos pos) {

    public static Location from(FriendlyByteBuf buf) {
        return new Location(buf.readUtf(), buf.readGlobalPos());
    }

    public void to(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeGlobalPos(pos);
    }
}