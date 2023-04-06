package earth.terrarium.prometheus.common.menus;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record WrappedPlayerContainer(Player player) implements Container {

    @Override
    public int getContainerSize() {
        return player.getInventory().getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return player.getInventory().isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return player.getInventory().getItem(index);
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        return player.getInventory().removeItem(index, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return player.getInventory().removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        player.getInventory().setItem(index, stack);
        setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return player.getInventory().getMaxStackSize();
    }

    @Override
    public void setChanged() {
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
        return player.getInventory().canPlaceItem(index, stack);
    }

    @Override
    public void clearContent() {
        player.getInventory().clearContent();
    }
}