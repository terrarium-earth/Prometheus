package earth.terrarium.prometheus.common.menus.content;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record MemberRolesContent(
    List<MemberRolesContent.MemberRole> roles,
    UUID person
) {

    public static final ByteCodec<MemberRolesContent> BYTE_CODEC = ObjectByteCodec.create(
            MemberRolesContent.MemberRole.BYTE_CODEC.listOf().fieldOf(MemberRolesContent::roles),
            ByteCodec.UUID.fieldOf(MemberRolesContent::person),
            MemberRolesContent::new
    );

    public record MemberRole(UUID id, String name, boolean selected, boolean canGive) {

        public static final ByteCodec<MemberRole> BYTE_CODEC = ObjectByteCodec.create(
                ByteCodec.UUID.fieldOf(MemberRole::id),
                ByteCodec.STRING.fieldOf(MemberRole::name),
                ByteCodec.BOOLEAN.fieldOf(MemberRole::selected),
                ByteCodec.BOOLEAN.fieldOf(MemberRole::canGive),
                MemberRole::new
        );

        public static MemberRolesContent.MemberRole of(RoleEntry entry, boolean selected, boolean canGive) {
            CosmeticOptions options = entry.role().getOption(CosmeticOptions.SERIALIZER);
            return new MemberRolesContent.MemberRole(entry.id(), Objects.requireNonNull(options).display(), selected, canGive);
        }
    }
}
