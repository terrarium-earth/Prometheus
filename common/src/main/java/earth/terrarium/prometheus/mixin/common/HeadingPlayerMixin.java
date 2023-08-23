package earth.terrarium.prometheus.mixin.common;

import earth.terrarium.prometheus.common.handlers.heading.Heading;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEntityHook;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class HeadingPlayerMixin extends LivingEntity implements HeadingEntityHook {

    @Unique
    private Component prometheus$headingText = null;

    @Unique
    private Heading prometheus$heading = Heading.NONE;

    protected HeadingPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void prometheus$setHeading(Heading heading) {
        this.prometheus$heading = heading;
    }

    @Override
    public Heading prometheus$getHeading() {
        return this.prometheus$heading;
    }

    @Override
    public Component prometheus$getHeadingText() {
        return this.prometheus$headingText;
    }

    @Override
    public void prometheus$setHeadingText(@Nullable Component text) {
        this.prometheus$headingText = text;
    }
}
