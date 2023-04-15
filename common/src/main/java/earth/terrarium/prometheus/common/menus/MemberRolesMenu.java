package earth.terrarium.prometheus.common.menus;

import com.mojang.authlib.GameProfile;
import earth.terrarium.prometheus.common.handlers.role.*;
import earth.terrarium.prometheus.common.roles.CosmeticOptions;
import earth.terrarium.prometheus.common.registries.ModMenus;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class MemberRolesMenu extends AbstractContainerMenu {

    private final List<MemberRole> roles;
    private final UUID person;

    public MemberRolesMenu(int i, Inventory inventory, FriendlyByteBuf buf) {
        this(i, buf.readList(MemberRole::of), buf.readUUID());
    }

    public MemberRolesMenu(int id, List<MemberRole> roles, UUID person) {
        super(ModMenus.MEMBER_ROLES.get(), id);
        this.roles = roles;
        this.person = person;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public List<MemberRole> getRoles() {
        return roles;
    }

    public UUID getPerson() {
        return person;
    }

    public static void write(FriendlyByteBuf buf, List<MemberRole> roles, UUID selected) {
        buf.writeCollection(roles, (buffer, role) -> role.write(buffer));
        buf.writeUUID(selected);
    }

    public static void open(ServerPlayer player, GameProfile profile) {
        List<RoleEntry> roles = RoleHandler.roles(player).roles();
        Set<UUID> editable = RoleHandler.getEditableRoles(player);
        Set<UUID> selected = RoleHandler.getRolesForPlayer(player, profile.getId());

        List<MemberRole> packetRoles = roles.stream()
                .filter(Predicate.not(RoleEntry::isDefault))
                .map(entry -> MemberRole.of(entry, selected.contains(entry.id()), editable.contains(entry.id())))
                .toList();
        ModUtils.openMenu(player,
                (id, inventory, p) -> new MemberRolesMenu(id, packetRoles, profile.getId()),
                Component.literal(profile.getName()),
                buf -> MemberRolesMenu.write(buf, packetRoles, profile.getId())
        );
    }

    public record MemberRole(UUID id, String name, boolean selected, boolean canGive) {

        public static MemberRole of(RoleEntry entry, boolean selected, boolean canGive) {
            CosmeticOptions options = entry.role().getOption(CosmeticOptions.SERIALIZER);
            return new MemberRole(entry.id(), options.display(), selected, canGive);
        }

        public static MemberRole of(FriendlyByteBuf buf) {
            return new MemberRole(buf.readUUID(), buf.readUtf(), buf.readBoolean(), buf.readBoolean());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeUUID(id);
            buf.writeUtf(name);
            buf.writeBoolean(selected);
            buf.writeBoolean(canGive);
        }
    }
}
