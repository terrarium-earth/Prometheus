package earth.terrarium.prometheus.common.handlers.role;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import earth.terrarium.prometheus.api.roles.options.RoleOption;
import earth.terrarium.prometheus.api.roles.options.RoleOptionSerializer;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class OptionRegistry {

    public static final OptionRegistry INSTANCE = new OptionRegistry();
    private static final RoleOptionSerializer<DummyOption> DUMMY_SERIALIZER = RoleOptionSerializer.of(new ResourceLocation("noop"), 0, Codec.unit(DummyOption::new), new DummyOption());

    private final Map<ResourceLocation, RoleOptionSerializer<?>> serializers = new HashMap<>();
    private final Object2IntMap<ResourceLocation> types = new Object2IntArrayMap<>();

    private boolean frozen = false;

    private OptionRegistry() {}

    @ApiStatus.Internal
    public static void freeze() {
        INSTANCE.frozen = true;
    }

    /**
     * Registers a new role option serializer.
     */
    public <T extends RoleOption<T>> void register(RoleOptionSerializer<T> serializer) {
        if (this.frozen) {
            throw new IllegalStateException("Cannot register role option after the registry has been frozen.");
        }
        if (this.serializers.containsKey(serializer.id())) {
            throw new IllegalStateException("Attempted to register role option with duplicate id: " + serializer.id());
        }
        this.serializers.put(serializer.id(), serializer);
        int typeVersion = this.types.getInt(serializer.type());
        if (typeVersion < serializer.version()) {
            this.types.put(serializer.type(), serializer.version());
        }
    }

    @Nullable
    public RoleOptionSerializer<?> get(ResourceLocation id) {
        return this.serializers.get(id);
    }

    public List<RoleOptionSerializer<?>> getAll() {
        return List.copyOf(this.serializers.values());
    }

    @SuppressWarnings("unchecked")
    public static Function<ResourceLocation, Codec<RoleOption<?>>> codec() {
        return type -> (Codec<RoleOption<?>>) decode(type)
                .map(RoleOptionSerializer::codec)
                .result().orElse(null);
    }

    private static DataResult<RoleOptionSerializer<?>> decode(ResourceLocation id) {
        RoleOptionSerializer<?> serializer = INSTANCE.get(id);
        if (serializer == null) {
            //TODO Add conditional logging here
            return DataResult.success(DUMMY_SERIALIZER);
        }
        if (serializer.version() < INSTANCE.types.getInt(serializer.type())) {
            //TODO Switch to LOGGER!
            System.out.println("Serializer " + id + " is outdated the current version is " + INSTANCE.types.getInt(serializer.type()) + ".");
        }
        return DataResult.success(serializer);
    }

    private static final class DummyOption implements RoleOption<DummyOption> {
        @Override
        public RoleOptionSerializer<DummyOption> serializer() {
            return DUMMY_SERIALIZER;
        }
    }
}
