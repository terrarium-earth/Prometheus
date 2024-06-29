package earth.terrarium.prometheus.common.handlers.heading;

import com.mojang.serialization.Codec;
import com.teamresourceful.resourcefullib.common.utils.files.CodecSavedData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeadingHandler {

    private static final CodecSavedData.Factory<Map<UUID, Heading>> DATA = CodecSavedData
        .create(Codec.unboundedMap(UUIDUtil.STRING_CODEC, Heading.CODEC), "prometheus_headings")
        .defaultValue(HashMap::new)
        .global()
        .clean();

    public static boolean set(Player player, Heading heading) {
        if (!heading.hasPermission(player)) return false;
        if (!(player instanceof ServerPlayer serverPlayer)) return false;
        var data = DATA.create(serverPlayer.serverLevel());
        data.get().put(player.getUUID(), heading);
        if (player instanceof HeadingEntityHook hook) {
            hook.prometheus$setHeadingAndUpdate(heading);
            HeadingEvents.sendToOnlinePlayers(player.getServer(), player, heading, heading.getDisplayName());
        }
        data.setDirty();
        return true;
    }

    public static Heading get(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return DATA.create(serverPlayer.serverLevel()).get().getOrDefault(player.getUUID(), Heading.NONE);
        }
        return Heading.NONE;
    }
}
