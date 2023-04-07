package earth.terrarium.prometheus.api.roles.options;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface RoleOptionSerializer<T extends RoleOption<T>> {

    default ResourceLocation id() {
        return new ResourceLocation(type().getNamespace(), type().getPath() + "/v" + version());
    }

    ResourceLocation type();

    int version();

    Codec<T> codec();

    default @Nullable T defaultValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default T cast(RoleOption<?> data) {
        return (T) data;
    }

    static <T extends RoleOption<T>> RoleOptionSerializer<T> of(ResourceLocation id, int version, Codec<T> codec, @Nullable T defaultValue) {
        return new RoleOptionSerializer<>() {
            @Override
            public ResourceLocation type() {
                return id;
            }

            @Override
            public int version() {
                return version;
            }

            @Override
            public Codec<T> codec() {
                return codec;
            }

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }
}
