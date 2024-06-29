package earth.terrarium.prometheus.common.menus.content;

import com.teamresourceful.bytecodecs.base.ByteCodec;
import com.teamresourceful.bytecodecs.base.object.ObjectByteCodec;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;

import java.util.List;
import java.util.UUID;

public record RoleEditContent(
    List<RoleEntry> roles,
    Role selected,
    UUID selectedId
) {

    public static final ByteCodec<RoleEditContent> BYTE_CODEC = ObjectByteCodec.create(
            RoleEntry.BYTE_CODEC.listOf().optionalFieldOf(CodecExtras.optionalFor(RoleEditContent::roles)),
            ByteCodec.UUID.fieldOf(RoleEditContent::selectedId),
            (r, selectedId) -> new RoleEditContent(r.orElse(null), selectedId)
    );

    public RoleEditContent(List<RoleEntry> roles, UUID selected) {
        this(roles, roles == null ? null : roles.stream()
            .filter(entry -> entry.id().equals(selected))
            .findFirst()
            .map(RoleEntry::role)
            .orElse(null), selected);
    }
}
