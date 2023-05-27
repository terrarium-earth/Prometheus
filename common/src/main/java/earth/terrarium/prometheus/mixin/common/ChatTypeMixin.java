package earth.terrarium.prometheus.mixin.common;

import earth.terrarium.prometheus.common.handlers.nickname.NickedEntityHook;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatType.class)
public class ChatTypeMixin {

    @Inject(
        method = "bind(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/network/chat/ChatType$Bound;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void prometheus$bind(ResourceKey<ChatType> resourceKey, Entity entity, CallbackInfoReturnable<ChatType.Bound> cir) {
        if (entity instanceof NickedEntityHook hook && entity instanceof Player player) {
            if (hook.prometheus$getNickname() != null) {
                MutableComponent teamName = PlayerTeam.formatNameForTeam(player.getTeam(), hook.prometheus$getNickname());
                String username = player.getGameProfile().getName();
                Component name = teamName.withStyle(
                    (style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + username + " "))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(player.getType(), player.getUUID(), player.getName())))
                        .withInsertion(username)
                );

                cir.setReturnValue(ChatType.bind(resourceKey, entity.level().registryAccess(), name));
            }
        }
    }
}
