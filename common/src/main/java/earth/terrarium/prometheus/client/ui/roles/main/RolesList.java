package earth.terrarium.prometheus.client.ui.roles.main;

import earth.terrarium.olympus.client.components.lists.EntryListWidget;
import earth.terrarium.prometheus.common.handlers.role.RoleEntry;
import earth.terrarium.prometheus.common.menus.content.RolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundOpenRolePacket;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;

public class RolesList extends EntryListWidget<Void> {

    private final RolesContent content;

    public RolesList(@Nullable EntryListWidget<Void> list, int width, int height, RolesContent content) {
        super(list, width, height);
        this.content = content;
    }

    @Override
    public void update() {
        clear();

        for (int i = 0; i < content.getRoles().size(); i++) {
            final int index = i;
            RoleEntry role = content.getRoles().get(i);
            RoleListEntry entry = new RoleListEntry(
                role.role().getNonNullOption(CosmeticOptions.SERIALIZER).display(),
                role.id().equals(Util.NIL_UUID) || i == 0 ? null : () -> {
                    RoleEntry temp = content.getRoles().get(index - 1);
                    content.getRoles().set(index - 1, role);
                    content.getRoles().set(index, temp);
                    update();
                },
                role.id().equals(Util.NIL_UUID) || i == content.getRoles().size() - 2 ? null : () -> {
                    RoleEntry temp = content.getRoles().get(index + 1);
                    content.getRoles().set(index + 1, role);
                    content.getRoles().set(index, temp);
                    update();
                },
                content.areRolesDifferent() ? null : () -> {
                    NetworkHandler.CHANNEL.sendToServer(new ServerboundOpenRolePacket(role.id()));
                    update();
                },
                role.id().equals(Util.NIL_UUID) ? null : () -> {
                    content.remove(role.id());
                    update();
                }
            );
            add(entry);
        }
    }
}
