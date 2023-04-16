package earth.terrarium.prometheus.common.menus;

import earth.terrarium.prometheus.common.handlers.commands.DynamicCommandHandler;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import earth.terrarium.prometheus.common.network.messages.server.SaveCommandPacket;
import earth.terrarium.prometheus.common.registries.ModMenus;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EditCommandMenu extends AbstractContainerMenu {

    private List<String> lines;
    private final String id;

    public EditCommandMenu(int i, Inventory ignored, FriendlyByteBuf buf) {
        this(i, buf.readList(FriendlyByteBuf::readUtf), buf.readUtf());
    }

    public EditCommandMenu(int id, List<String> lines, String commandId) {
        super(ModMenus.EDIT_COMMAND.get(), id);
        this.lines = lines;
        this.id = commandId;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    public List<String> lines() {
        return lines;
    }

    public void saveLines(List<String> lines) {
        this.lines = new ArrayList<>();
        this.lines.addAll(lines);
        NetworkHandler.CHANNEL.sendToServer(new SaveCommandPacket(id, lines));
    }

    public String id() {
        return id;
    }

    public static void write(FriendlyByteBuf buf, List<String> lines, String id) {
        buf.writeCollection(lines, FriendlyByteBuf::writeUtf);
        buf.writeUtf(id);
    }

    public static void open(ServerPlayer player, String commandId) {
        List<String> lines = DynamicCommandHandler.getCommand(player.getLevel(), commandId);
        ModUtils.openMenu(player,
            (id, inventory, p) -> new EditCommandMenu(id, lines, commandId),
            CommonComponents.EMPTY,
            buf -> EditCommandMenu.write(buf, lines, commandId)
        );
    }
}
