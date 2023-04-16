package earth.terrarium.prometheus.common.handlers.role;

import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RoleMap implements Iterable<RoleEntry> {

    private final List<RoleEntry> roles = new ArrayList<>();
    private RoleEntry defaultRole = new RoleEntry(DefaultRole.DEFAULT_ROLE, DefaultRole.create());

    public void set(UUID uuid, Role role) {
        if (uuid == null)
            uuid = ModUtils.generate(id -> getIndex(id) == -1 && !DefaultRole.DEFAULT_ROLE.equals(id), UUID::randomUUID);
        if (uuid.equals(DefaultRole.DEFAULT_ROLE)) {
            defaultRole = new RoleEntry(uuid, role);
            return;
        }
        int index = getIndex(uuid);
        if (index == -1) {
            roles.add(new RoleEntry(uuid, role));
        } else {
            roles.set(index, new RoleEntry(uuid, role));
        }
    }

    public void reorder(List<UUID> ids) {
        List<RoleEntry> newRoles = new ArrayList<>();
        for (UUID id : ids) {
            for (RoleEntry entry : roles) {
                if (entry.id().equals(id)) {
                    newRoles.add(entry);
                    break;
                }
            }
        }
        roles.clear();
        roles.addAll(newRoles);
    }

    public List<RoleEntry> roles(Set<UUID> ids) {
        List<RoleEntry> roles = new ArrayList<>();
        for (RoleEntry entry : this.roles) {
            if (ids.contains(entry.id())) {
                roles.add(entry);
            }
        }
        roles.add(defaultRole);
        return roles;
    }

    public List<RoleEntry> roles() {
        List<RoleEntry> roles = new ArrayList<>(this.roles);
        roles.add(defaultRole);
        return roles;
    }

    public Set<UUID> ids() {
        Set<UUID> identifiers = new LinkedHashSet<>();
        for (RoleEntry pair : roles) {
            identifiers.add(pair.id());
        }
        return identifiers;
    }

    private int getIndex(UUID uuid) {
        for (int i = 0; i < roles.size(); i++) {
            if (Objects.equals(roles.get(i).id(), uuid)) {
                return i;
            }
        }
        return -1;
    }

    public void load(CompoundTag tag) {
        roles.clear();
        for (Tag roleTag : tag.getList("roles", Tag.TAG_COMPOUND)) {
            CompoundTag compoundRole = (CompoundTag) roleTag;
            UUID uuid = UUID.fromString(compoundRole.getString("uuid"));
            Role role = Role.fromTag(compoundRole.getCompound("role"));
            if (uuid.equals(DefaultRole.DEFAULT_ROLE)) {
                defaultRole = new RoleEntry(uuid, role);
            } else {
                roles.add(new RoleEntry(uuid, role));
            }
        }
    }

    public CompoundTag save(CompoundTag tag) {
        ListTag roles = new ListTag();
        List<RoleEntry> rolesList = new ArrayList<>(this.roles);
        rolesList.add(defaultRole);
        for (RoleEntry entry : rolesList) {
            CompoundTag role = new CompoundTag();
            role.putString("uuid", entry.id().toString());
            role.put("role", entry.role().toTag());
            roles.add(role);
        }
        tag.put("roles", roles);
        return tag;
    }

    @NotNull
    @Override
    public Iterator<RoleEntry> iterator() {
        return roles.iterator();
    }
}
