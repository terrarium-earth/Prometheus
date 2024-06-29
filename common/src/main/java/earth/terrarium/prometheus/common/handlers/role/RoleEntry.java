package earth.terrarium.prometheus.common.handlers.role;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;

import java.util.UUID;

public record RoleEntry(UUID id, Role role) {

    public static final ByteCodec<RoleEntry> BYTE_CODEC = ObjectByteCodec.create(
            ByteCodec.UUID.fieldOf(RoleEntry::id),
            Role.BYTE_CODEC.fieldOf(RoleEntry::role),
            RoleEntry::new
    );

    public boolean isDefault() {
        return id.equals(DefaultRole.DEFAULT_ROLE);
    }
}
