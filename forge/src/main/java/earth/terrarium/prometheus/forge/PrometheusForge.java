package earth.terrarium.prometheus.forge;

import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.commands.ModCommands;
import earth.terrarium.prometheus.common.handlers.MuteHandler;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEvents;
import earth.terrarium.prometheus.common.handlers.nickname.NicknameEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(Prometheus.MOD_ID)
public class PrometheusForge {

    @SuppressWarnings("Convert2MethodRef")
    public PrometheusForge() {
        Prometheus.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
            earth.terrarium.prometheus.client.PrometheusClient.init()
        );
        MinecraftForge.EVENT_BUS.addListener(PrometheusForge::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForge::onChatMessage);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForge::onPlayerJoin);
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    private static void onChatMessage(ServerChatEvent event) {
        if (!MuteHandler.canMessageGoThrough(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    private static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HeadingEvents.onJoin(player);
            NicknameEvents.onJoin(player);
        }
    }
}