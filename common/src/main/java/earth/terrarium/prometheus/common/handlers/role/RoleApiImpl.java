package earth.terrarium.prometheus.common.handlers.role;

import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RoleApiImpl implements RoleApi {

    @Override
    public <T extends RoleOption<T>> T getOption(Player player, RoleOptionSerializer<T> serializer) {
        if (player instanceof RoleEntityHook holder) {
            Role role = holder.prometheus$getHighestRole();
            if (role != null) {
                return role.getOption(serializer);
            }
        }
        return null;
    }

    @Override
    public <T extends RoleOption<T>> @Nullable T forceGetOption(Level level, UUID player, RoleOptionSerializer<T> serializer) {
        Role role = RoleHandler.getHighestRole(level, player);
        if (role != null) {
            return role.getOption(serializer);
        }
        return null;
    }
}
