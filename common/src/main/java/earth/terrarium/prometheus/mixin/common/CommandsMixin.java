package earth.terrarium.prometheus.mixin.common;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissionHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
        CommandPermissionHandler.registerPermissions(this.dispatcher);
    }
}
