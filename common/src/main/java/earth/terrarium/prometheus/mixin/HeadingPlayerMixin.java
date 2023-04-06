package earth.terrarium.prometheus.mixin;

import earth.terrarium.prometheus.common.handlers.heading.Heading;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEntityHook;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@SuppressWarnings("WrongEntityDataParameterClass")
@Mixin(Player.class)
public abstract class HeadingPlayerMixin extends LivingEntity implements HeadingEntityHook {

    private static final EntityDataAccessor<Optional<Component>> PROMETHEUS$TEXT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Byte> PROMETHEUS$HEADING = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);

    protected HeadingPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    public void prometheus$defineSynchedData(CallbackInfo ci) {
        this.entityData.define(PROMETHEUS$TEXT, Optional.empty());
        this.entityData.define(PROMETHEUS$HEADING, (byte) 0);
    }

    @Override
    public void prometheus$setHeading(Heading heading) {
        this.entityData.set(PROMETHEUS$HEADING, (byte) heading.ordinal());
    }

    @Override
    public Heading prometheus$getHeading() {
        return Heading.fromId(this.entityData.get(PROMETHEUS$HEADING));
    }

    @Override
    public Optional<Component> prometheus$getHeadingText() {
        return this.entityData.get(PROMETHEUS$TEXT);
    }

    @Override
    public void prometheus$setHeadingText(@Nullable Component text) {
        this.entityData.set(PROMETHEUS$TEXT, Optional.ofNullable(text));
    }
}
