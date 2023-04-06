package earth.terrarium.prometheus.common.menus;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EquipmentItemSlot extends Slot {

    private static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{
            new ResourceLocation("item/empty_armor_slot_boots"),
            new ResourceLocation("item/empty_armor_slot_leggings"),
            new ResourceLocation("item/empty_armor_slot_chestplate"),
            new ResourceLocation("item/empty_armor_slot_helmet")
    };
    private static final ResourceLocation[] TEXTURE_EMPTY_HAND_SLOTS = new ResourceLocation[]{
            new ResourceLocation("item/empty_armor_slot_shield"),
            new ResourceLocation("item/empty_armor_slot_shield")
    };

    private final EquipmentSlot slot;
    private final Player player;

    public EquipmentItemSlot(EquipmentSlot slot, Player player, Container container, int i, int j, int k) {
        super(container, i, j, k);
        this.slot = slot;
        this.player = player;
    }

    @Override
    public void setByPlayer(@NotNull ItemStack stack) {
        Equipable equipable = Equipable.get(stack);
        if (equipable != null && player != null) {
            player.onEquipItem(slot, stack, this.getItem());
        }
        super.setByPlayer(stack);
    }

    @Override
    public int getMaxStackSize() {
        return slot.isArmor() ? 1 : super.getMaxStackSize();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return !slot.isArmor() || slot == Mob.getEquipmentSlotForItem(stack);
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return super.mayPickup(player);
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        if (slot.getType() == EquipmentSlot.Type.HAND)
            return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_HAND_SLOTS[slot.getIndex()]);
        return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[slot.getIndex()]);
    }
}