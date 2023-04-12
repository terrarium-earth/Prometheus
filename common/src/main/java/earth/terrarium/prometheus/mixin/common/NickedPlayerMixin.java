package earth.terrarium.prometheus.mixin.common;

import earth.terrarium.prometheus.common.handlers.nickname.NickedEntityHook;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class NickedPlayerMixin extends LivingEntity implements NickedEntityHook {

    private Component prometheus$nickname;

    protected NickedPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void prometheus$setNickname(Component nickname) {
        this.prometheus$nickname = nickname;
    }

    @Override
    public Component prometheus$getNickname() {
        return this.prometheus$nickname;
    }
}
