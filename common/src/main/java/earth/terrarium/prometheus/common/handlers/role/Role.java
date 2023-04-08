package earth.terrarium.prometheus.common.handlers.role;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import earth.terrarium.prometheus.api.TriState;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import earth.terrarium.prometheus.common.handlers.role.options.OptionRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Role(Map<String, TriState> permissions, Map<ResourceLocation, RoleOption<?>> options) {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Codec<TriState> STATE_CODEC = Codec.BYTE.xmap(TriState::of, state -> (byte)state.ordinal());
    public static final Codec<Role> CODEC = RecordCodecBuilder.create(instnace -> instnace.group(
            Codec.unboundedMap(Codec.STRING, STATE_CODEC).fieldOf("permissions").orElse(err -> { LOGGER.error(err); }, new HashMap<>()).forGetter(Role::permissions),
            new DispatchMapCodec<>(ResourceLocation.CODEC, OptionRegistry.codec()).fieldOf("options").orElse(err -> { LOGGER.error(err); }, new HashMap<>()).forGetter(Role::options)
    ).apply(instnace, Role::new));

    public Role() {
        this(new HashMap<>(), new HashMap<>());
    }

    public void setPermission(String permission, TriState state) {
        permissions.put(permission, state);
    }

    public void setData(RoleOption<?> data) {
        options.put(data.serializer().id(), data);
    }

    /**
     * @return returns Optional.empty() if the data is not present
     * @throws IllegalArgumentException if serializer on data does not match data passed in.
     */
    public <T extends RoleOption<T>> Optional<T> getOptionalOption(RoleOptionSerializer<T> serializer) {
        RoleOption<?> data = options.get(serializer.id());
        if (data != null) {
            if (data.serializer().equals(serializer)) {
                return Optional.of(serializer.cast(data));
            }
            throw new IllegalArgumentException("RoleOptionSerializer type does not match RoleOption");
        }
        return Optional.empty();
    }

    /**
     * @return returns null or default if data not found.
     * @throws IllegalArgumentException if serializer on data does not match data passed in.
     */
    @Nullable
    public <T extends RoleOption<T>> T getOption(RoleOptionSerializer<T> serializer) {
        return getOptionalOption(serializer).orElse(serializer.defaultValue());
    }

    public CompoundTag toTag() {
        Tag tag = CODEC.encodeStart(NbtOps.INSTANCE, this).
                resultOrPartial(LOGGER::error)
                .orElse(new CompoundTag());
        return (CompoundTag) tag;
    }

    public static Role fromTag(CompoundTag tag) {
        return CODEC.parse(NbtOps.INSTANCE, tag)
                .resultOrPartial(LOGGER::error)
                .orElse(new Role());
    }
}
