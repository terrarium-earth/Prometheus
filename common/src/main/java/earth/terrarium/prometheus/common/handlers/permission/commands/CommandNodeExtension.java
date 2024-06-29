package earth.terrarium.prometheus.common.handlers.permission.commands;

import com.mojang.brigadier.tree.CommandNode;

public interface CommandNodeExtension {

    void prometheus$setPermission(String permission);

    String prometheus$getPermission();

    void prometheus$removeChild(CommandNode<?> child);
}
