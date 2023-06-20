package earth.terrarium.prometheus.common.menus.content;

import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.UUID;

public record RoleEditContent(
    List<RoleEntry> roles,
    Role selected,
    UUID selectedId
) {

    public RoleEditContent(List<RoleEntry> roles, UUID selected) {
        this(roles, roles == null ? null : roles.stream()
            .filter(entry -> entry.id().equals(selected))
            .findFirst()
            .map(RoleEntry::role)
            .orElse(null), selected);
    }

    public int getIndexOf(UUID id) {
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).id().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(roles, (buffer, role) -> {
            buffer.writeUUID(role.id());
            role.role().toBuffer(buffer);
        });
        buf.writeUUID(selectedId);
    }

    public static RoleEditContent read(FriendlyByteBuf buf) {
        List<RoleEntry> roles = buf.readList(buffer -> new RoleEntry(buffer.readUUID(), Role.fromBuffer(buffer)));
        for (RoleEntry role : roles) {
            if (role.role() == null) {
                return new RoleEditContent(null, buf.readUUID());
            }
        }
        return new RoleEditContent(roles, buf.readUUID());
    }
}
