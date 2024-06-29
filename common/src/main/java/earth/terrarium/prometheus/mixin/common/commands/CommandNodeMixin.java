package earth.terrarium.prometheus.mixin.common.commands;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import earth.terrarium.prometheus.common.handlers.permission.commands.CommandNodeExtension;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(value = CommandNode.class, remap = false)
public class CommandNodeMixin<S> implements CommandNodeExtension {

    @Shadow @Final private Map<String, CommandNode<S>> children;
    @Shadow @Final private Map<String, LiteralCommandNode<S>> literals;
    @Shadow @Final private Map<String, ArgumentCommandNode<S, ?>> arguments;
    @Unique
    private String prometheus$permission = null;

    @Override
    public void prometheus$setPermission(String permission) {
        prometheus$permission = permission;
    }

    @Override
    public String prometheus$getPermission() {
        return prometheus$permission;
    }

    @Override
    public void prometheus$removeChild(CommandNode<?> child) {
        this.children.remove(child.getName());
        this.literals.remove(child.getName());
        this.arguments.remove(child.getName());
    }
}
