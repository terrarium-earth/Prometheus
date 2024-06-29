package earth.terrarium.prometheus.mixin.common.commands;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.RootCommandNode;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void prometheus$onInit(Commands.CommandSelection commandSelection, CommandBuildContext commandBuildContext, CallbackInfo ci) {
        CommandPermissions.registerPermissions(this.dispatcher);
    }

    @WrapOperation(
        method = "sendCommands",
        at = @At(
            value = "NEW",
            target = "(Lcom/mojang/brigadier/tree/RootCommandNode;)Lnet/minecraft/network/protocol/game/ClientboundCommandsPacket;"
        )
    )
    private ClientboundCommandsPacket prometheus$sendCommands(RootCommandNode<SharedSuggestionProvider> root, Operation<ClientboundCommandsPacket> operation) {
        return operation.call(CommandPermissions.sendCommands(root));
    }
}
