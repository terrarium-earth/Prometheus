package earth.terrarium.prometheus.api.roles.options;

import com.mojang.serialization.Codec;
import com.teamresourceful.bytecodecs.base.ByteCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface RoleOptionSerializer<T extends RoleOption<T>> {

    default ResourceLocation id() {
        return ResourceLocation.fromNamespaceAndPath(type().getNamespace(), type().getPath() + "/v" + version());
    }

    ResourceLocation type();

    int version();

    Codec<T> codec();

    ByteCodec<T> byteCodec();

    default @Nullable T defaultValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    default T cast(RoleOption<?> data) {
        return (T) data;
    }

    static <T extends RoleOption<T>> RoleOptionSerializer<T> of(
            ResourceLocation id,
            int version,
            Codec<T> codec,
            ByteCodec<T> byteCodec,
            @Nullable T defaultValue
    ) {
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
            public ByteCodec<T> byteCodec() {
                return byteCodec;
            }

            @Override
            public T defaultValue() {
                return defaultValue;
            }
        };
    }
}
