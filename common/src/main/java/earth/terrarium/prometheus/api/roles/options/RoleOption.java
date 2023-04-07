package earth.terrarium.prometheus.api.roles.options;

public interface RoleOption<T extends RoleOption<T>> {

    RoleOptionSerializer<T> serializer();
}
