package earth.terrarium.prometheus.common.menus.content;

import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record MemberRolesContent(
    List<MemberRolesContent.MemberRole> roles,
    UUID person
) {

    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(roles, (buffer, role) -> role.write(buffer));
        buf.writeUUID(person);
    }

    public static MemberRolesContent read(FriendlyByteBuf buf) {
        return new MemberRolesContent(buf.readList(MemberRolesContent.MemberRole::of), buf.readUUID());
    }

    public record MemberRole(UUID id, String name, boolean selected, boolean canGive) {

        public static MemberRolesContent.MemberRole of(RoleEntry entry, boolean selected, boolean canGive) {
            CosmeticOptions options = entry.role().getOption(CosmeticOptions.SERIALIZER);
            return new MemberRolesContent.MemberRole(entry.id(), Objects.requireNonNull(options).display(), selected, canGive);
        }

        public static MemberRolesContent.MemberRole of(FriendlyByteBuf buf) {
            return new MemberRolesContent.MemberRole(buf.readUUID(), buf.readUtf(), buf.readBoolean(), buf.readBoolean());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeUUID(id);
            buf.writeUtf(name);
            buf.writeBoolean(selected);
            buf.writeBoolean(canGive);
        }
    }
}
