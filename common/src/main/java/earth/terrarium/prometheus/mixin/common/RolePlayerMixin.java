package earth.terrarium.prometheus.mixin.common;

import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntityHook;
import earth.terrarium.prometheus.common.handlers.role.RoleHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class RolePlayerMixin extends LivingEntity implements RoleEntityHook {

    private Role prometheus$highestRole;

    protected RolePlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void prometheus$updateHighestRole() {
        this.prometheus$highestRole = RoleHandler.getHighestRole((Player) (Object) this);
    }

    @Override
    public Role prometheus$getHighestRole() {
        if (this.prometheus$highestRole == null) {
            this.prometheus$updateHighestRole();
        }
        return this.prometheus$highestRole;
    }
}
