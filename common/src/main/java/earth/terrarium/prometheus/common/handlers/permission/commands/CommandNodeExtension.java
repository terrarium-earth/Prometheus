package earth.terrarium.prometheus.common.handlers.permission.commands;

import com.mojang.brigadier.tree.CommandNode;

public interface CommandNodeExtension {

    void prometheus$removeChild(CommandNode<?> child);
}
