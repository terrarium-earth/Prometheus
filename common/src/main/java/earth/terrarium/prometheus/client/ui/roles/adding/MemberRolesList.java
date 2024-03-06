package earth.terrarium.prometheus.client.ui.roles.adding;

import earth.terrarium.olympus.client.components.lists.EntryListWidget;
import earth.terrarium.prometheus.common.menus.content.MemberRolesContent;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.roles.ServerboundMemberRolesPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MemberRolesList extends EntryListWidget<UUID> {

    private final UUID person;
    private final MemberRolesContent content;
    private final Object2BooleanMap<UUID> roles;
    private final Runnable saver;

    public MemberRolesList(@Nullable EntryListWidget<UUID> list, int width, int height, UUID person, MemberRolesContent content, Object2BooleanMap<UUID> roles, Runnable saver) {
        super(list, width, height);
        this.person = person;
        this.content = content;
        this.roles = roles;
        this.saver = saver;
    }

    public void save() {
        update();
        NetworkHandler.CHANNEL.sendToServer(new ServerboundMemberRolesPacket(person, roles));
        saver.run();
    }

    @Override
    public void update() {
        clear();

        for (MemberRolesContent.MemberRole role : content.roles()) {
            if (!this.roles.getOrDefault(role.id(), false)) continue;
            this.add(new MemberRoleListEntry(role.name(), role.id(), roles, this::save));
        }
    }
}
