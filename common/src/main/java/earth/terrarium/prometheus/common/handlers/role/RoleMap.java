package earth.terrarium.prometheus.common.handlers.role;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public class RoleMap {

    private static final UUID DEFAULT_ROLE = UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final List<Pair<UUID, Role>> roles = new ArrayList<>();
    private Role defaultRole = DefaultRole.create();

    public UUID addRole(Role role) {
        UUID uuid = generateUUID();
        roles.add(Pair.of(uuid, role));
        return uuid;
    }

    public UUID setRole(UUID uuid, Role role) {
        int index = getRoleIndex(uuid);
        if (index == -1) {
            roles.add(Pair.of(uuid, role));
        } else {
            roles.set(index, Pair.of(uuid, role));
        }
        return uuid;
    }

    public void reorder(List<UUID> ids) {
        List<Pair<UUID, Role>> newRoles = new ArrayList<>();
        for (UUID id : ids) {
            for (Pair<UUID, Role> pair : roles) {
                if (pair.getFirst().equals(id)) {
                    newRoles.add(pair);
                    break;
                }
            }
        }
        roles.clear();
        roles.addAll(newRoles);
    }

    public List<Role> getRoles(Set<UUID> ids) {
        List<Role> roles = new ArrayList<>();
        for (Pair<UUID, Role> pair : this.roles) {
            if (ids.contains(pair.getFirst())) {
                roles.add(pair.getSecond());
            }
        }
        roles.add(defaultRole);
        return roles;
    }

    public Role getRole(UUID uuid) {
        for (Pair<UUID, Role> pair : roles) {
            if (pair.getFirst().equals(uuid)) {
                return pair.getSecond();
            }
        }
        return null;
    }

    private int getRoleIndex(UUID uuid) {
        for (int i = 0; i < roles.size(); i++) {
            if (Objects.equals(roles.get(i).getFirst(), uuid)) {
                return i;
            }
        }
        return -1;
    }

    private UUID generateUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (hasRole(uuid) || DEFAULT_ROLE.equals(uuid));
        return uuid;
    }

    private boolean hasRole(UUID uuid) {
        return roles.stream().anyMatch(pair -> pair.getFirst().equals(uuid));
    }

    public Set<UUID> getIdentifiers() {
        Set<UUID> identifiers = new HashSet<>();
        for (Pair<UUID, Role> pair : roles) {
            identifiers.add(pair.getFirst());
        }
        return identifiers;
    }

    public void load(CompoundTag tag) {
        roles.clear();
        for (Tag roleTag : tag.getList("roles", Tag.TAG_COMPOUND)) {
            CompoundTag compoundRole = (CompoundTag) roleTag;
            UUID uuid = UUID.fromString(compoundRole.getString("uuid"));
            Role role = Role.fromTag(compoundRole.getCompound("role"));
            if (uuid.equals(DEFAULT_ROLE)) {
                defaultRole = role;
            } else {
                roles.add(Pair.of(uuid, role));
            }
        }
    }

    public CompoundTag save(CompoundTag tag) {
        ListTag roles = new ListTag();
        List<Pair<UUID, Role>> rolesList = new ArrayList<>(this.roles);
        rolesList.add(Pair.of(DEFAULT_ROLE, defaultRole));
        for (Pair<UUID, Role> pair : rolesList) {
            CompoundTag role = new CompoundTag();
            CompoundTag roleData = pair.getSecond().toTag();
            role.putString("uuid", pair.getFirst().toString());
            role.put("role", roleData);
            roles.add(role);
        }
        tag.put("roles", roles);
        return tag;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoleMap{\n");
        builder.append("roles=\n");
        int index = 0;
        for (Pair<UUID, Role> pair : roles) {
            builder.append(index).append(": ").append(pair.getFirst()).append(" - ").append(pair.getSecond()).append("\n");
        }
        builder.append("defaultRole=").append(defaultRole).append("\n");
        builder.append('}');
        return builder.toString();
    }
}
