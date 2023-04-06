package earth.terrarium.prometheus.common.menus;

import earth.terrarium.prometheus.common.registries.ModMenus;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LocationMenu extends AbstractContainerMenu {

    private final String commandPrefix;
    private final int max;
    private final List<Location> locations;

    public LocationMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, buf.readUtf(), buf.readVarInt(), buf.readList(Location::from));
    }

    public LocationMenu(int id, String commandPrefix, int max, List<Location> locations) {
        super(ModMenus.LOCATION.get(), id);
        this.commandPrefix = commandPrefix;
        this.max = max;
        this.locations = locations;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int i) {
        if (i < locations.size()) {
            Location location = locations.get(i);
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.closeContainer();
                ServerLevel level = serverPlayer.server.getLevel(location.pos.dimension());
                if (level != null) {
                    serverPlayer.teleportTo(
                            level,
                            location.pos.pos().getX(), location.pos.pos().getY(), location.pos.pos().getZ(),
                            serverPlayer.getYRot(), serverPlayer.getXRot()
                    );
                }
            }
        }
        return false;
    }

    public int getMax() {
        return max;
    }

    public boolean canModify() {
        return max != -1;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public static void write(FriendlyByteBuf buf, String commandPrefix, int max, List<Location> locations) {
        buf.writeUtf(commandPrefix);
        buf.writeVarInt(max);
        buf.writeCollection(locations, (buffer, entry) -> entry.to(buffer));
    }

    public record Location(String name, GlobalPos pos) {

        private static Location from(FriendlyByteBuf buf) {
            return new Location(buf.readUtf(), buf.readGlobalPos());
        }

        public void to(FriendlyByteBuf buf) {
            buf.writeUtf(name);
            buf.writeGlobalPos(pos);
        }
    }
}
