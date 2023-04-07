package earth.terrarium.prometheus.mixin;

import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import earth.terrarium.prometheus.common.handlers.permission.PermissionHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(Player.class)
public abstract class PermissionPlayerMixin extends LivingEntity implements PermissionHolder {

    private Map<String, TriState> prometheus$permissions;

    protected PermissionPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void prometheus$updatePermissions() {
        prometheus$permissions = RoleHandler.getPermissions((Player)((Object) this));
    }

    @Override
    public TriState prometheus$hasPermission(String permission) {
        return prometheus$permissions.getOrDefault(permission, TriState.UNDEFINED);
    }

    @Override
    public Map<String, TriState> prometheus$getPermissions() {
        return prometheus$permissions;
    }
}
