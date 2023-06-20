package earth.terrarium.prometheus.common.menus.content;

import earth.terrarium.prometheus.common.handlers.role.Role;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ChangeRolesPacket;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RolesContent {

    private final List<RoleEntry> uneditable;

    private List<RoleEntry> roles;
    private List<RoleEntry> newRoles;


    public RolesContent(List<RoleEntry> roles, int starting) {
        this.uneditable = new ArrayList<>();
        this.roles = new ArrayList<>();
        if (roles != null) {
            for (int i = 0; i < roles.size(); i++) {
                if (i >= starting) {
                    this.roles.add(roles.get(i));
                } else {
                    this.uneditable.add(roles.get(i));
                }
            }
            this.newRoles = new ArrayList<>(this.roles);
        } else {
            this.roles = null;
            this.newRoles = null;
        }
    }

    public boolean hasError() {
        return roles == null;
    }

    public void remove(UUID id) {
        newRoles.removeIf(pair -> pair.id().equals(id));
    }

    public List<RoleEntry> getRoles() {
        return newRoles;
    }

    public void reset() {
        this.newRoles = new ArrayList<>(roles);
    }

    public void save() {
        this.roles = new ArrayList<>(newRoles);
        List<UUID> ids = new ArrayList<>();
        uneditable.forEach(pair -> ids.add(pair.id()));
        roles.forEach(pair -> ids.add(pair.id()));
        NetworkHandler.CHANNEL.sendToServer(new ChangeRolesPacket(ids));
    }

    public boolean areRolesDifferent() {
        return !newRoles.equals(roles);
    }

    public void move(UUID id, boolean up) {
        int index = -1;
        for (int i = 0; i < newRoles.size(); i++) {
            if (newRoles.get(i).id().equals(id)) {
                index = i;
                break;
            }
        }
        if (index == -1) return;
        if (up) {
            if (index == 0) return;
            RoleEntry role = newRoles.remove(index);
            newRoles.add(index - 1, role);
        } else {
            if (index == newRoles.size() - 1) return;
            RoleEntry role = newRoles.remove(index);
            newRoles.add(index + 1, role);
        }
    }

    public void write(FriendlyByteBuf buf) {
        List<RoleEntry> roles = new ArrayList<>(uneditable);
        roles.addAll(newRoles);
        buf.writeCollection(roles, (buffer, pair) -> {
            buffer.writeUUID(pair.id());
            pair.role().toBuffer(buffer);
        });
        buf.writeVarInt(uneditable.size());
    }

    public static RolesContent read(FriendlyByteBuf buf) {
        List<RoleEntry> roles = buf.readList(buffer -> new RoleEntry(buffer.readUUID(), Role.fromBuffer(buffer)));
        for (RoleEntry role : roles) {
            if (role.role() == null) {
                return new RolesContent(null, buf.readVarInt());
            }
        }
        return new RolesContent(roles, buf.readVarInt());
    }
}
