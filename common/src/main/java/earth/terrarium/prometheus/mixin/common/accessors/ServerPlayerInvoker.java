package earth.terrarium.prometheus.mixin.common.accessors;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayer.class)
public interface ServerPlayerInvoker {

    @Invoker("nextContainerCounter")
    void invokeNextContainerCounter();

    @Invoker("initMenu")
    void invokeInitMenu(AbstractContainerMenu menu);
}
