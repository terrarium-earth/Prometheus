package earth.terrarium.prometheus.common.menus;

import earth.terrarium.prometheus.common.commands.admin.InvseeCommand;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InvseeMenu extends AbstractContainerMenu {

    private final Container openedContainer;
    private final UUID playerUUID;

    public InvseeMenu(int id, Inventory inventory, Player player, Container container, UUID uuid) {
        super(null, id);
        this.openedContainer = container;
        this.playerUUID = uuid;

        this.addSlot(new EquipmentItemSlot(EquipmentSlot.OFFHAND, player, container, 40, 34, 32));
        this.addSlot(new EquipmentItemSlot(EquipmentSlot.HEAD, player, container, 39, 53, 18));
        this.addSlot(new EquipmentItemSlot(EquipmentSlot.CHEST, player, container, 38, 53, 45));
        this.addSlot(new EquipmentItemSlot(EquipmentSlot.LEGS, player, container, 37, 107, 18));
        this.addSlot(new EquipmentItemSlot(EquipmentSlot.FEET, player, container, 36, 107, 45));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(container, j + (i + 1) * 9, 8 + j * 18, 66 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(container, i, 8 + i * 18, 124));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(inventory, j + (i + 1) * 9, 8 + j * 18, 156 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 214));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int i) {
        if (i == 100) {
            if (this.openedContainer instanceof WrappedPlayerContainer wrappedContainer) {
                InvseeCommand.openEnderChest(player, wrappedContainer.player());
            }
        }
        return false;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
