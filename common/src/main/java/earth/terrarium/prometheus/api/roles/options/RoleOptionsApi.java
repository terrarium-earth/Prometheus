package earth.terrarium.prometheus.api.roles.options;

import earth.terrarium.prometheus.api.ApiHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RoleOptionsApi {

    RoleOptionsApi API = ApiHelper.load(RoleOptionsApi.class);

    /**
     * Registers a new role option serializer.
     *
     * @param serializer the serializer to register.
     * @param <T>        the type of the role option.
     * @throws IllegalStateException    if the registry has been frozen.
     * @throws IllegalArgumentException if a serializer with the same id has already been registered.
     */
    <T extends RoleOption<T>> void register(RoleOptionSerializer<T> serializer);

    /**
     * Gets the serializer for the given id.
     *
     * @param id the id of the serializer.
     * @return the serializer, or null if no serializer with the given id has been registered.
     */
    @Nullable
    RoleOptionSerializer<?> get(ResourceLocation id);

    /**
     * Gets all registered serializers.
     *
     * @return a list of all registered serializers.
     */
    List<RoleOptionSerializer<?>> getAll();
}
