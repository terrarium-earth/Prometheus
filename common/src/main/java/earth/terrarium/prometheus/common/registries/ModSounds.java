package earth.terrarium.prometheus.common.registries;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.prometheus.Prometheus;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final ResourcefulRegistry<SoundEvent> SOUNDS = ResourcefulRegistries.create(BuiltInRegistries.SOUND_EVENT, Prometheus.MOD_ID);

    public static final RegistryEntry<SoundEvent> PING_1 = SOUNDS.register("ping_1", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Prometheus.MOD_ID, "ping_1")));
    public static final RegistryEntry<SoundEvent> PING_2 = SOUNDS.register("ping_2", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Prometheus.MOD_ID, "ping_2")));
    public static final RegistryEntry<SoundEvent> PING_3 = SOUNDS.register("ping_3", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Prometheus.MOD_ID, "ping_3")));
}
