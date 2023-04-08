package earth.terrarium.prometheus.common.constants;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ConstantComponents {

    public static final Component UNSAVED_CHANGES = Component.translatable("prometheus.ui.unsaved_changes").withStyle(ChatFormatting.RED);
    public static final Component ERROR_IN_LOGS = Component.literal("Error Occurred, Check Logs!");
    public static final Component REMOVE = Component.translatable("prometheus.ui.remove");
    public static final Component SAVE = Component.translatable("prometheus.ui.save");
    public static final Component EDIT = Component.translatable("prometheus.ui.edit");
    public static final Component MOVE_DOWN = Component.translatable("prometheus.ui.move_down");
    public static final Component MOVE_UP = Component.translatable("prometheus.ui.move_up");
    public static final Component UNDO = Component.translatable("prometheus.ui.undo");
    public static final Component ADD = Component.translatable("prometheus.ui.add");
}
