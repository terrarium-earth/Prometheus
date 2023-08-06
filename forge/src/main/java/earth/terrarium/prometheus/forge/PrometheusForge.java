package earth.terrarium.prometheus.forge;

import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.commands.ModCommands;
import earth.terrarium.prometheus.common.handlers.MuteHandler;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEvents;
import earth.terrarium.prometheus.common.handlers.nickname.NicknameEvents;
import earth.terrarium.prometheus.common.handlers.permission.PermissionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Prometheus.MOD_ID)
public class PrometheusForge {

    public PrometheusForge() {
        Prometheus.init();
        if (FMLEnvironment.dist.isClient()) {
            PrometheusForgeClient.init();
        }
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PrometheusForge::onCommonSetup);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForge::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForge::onChatMessage);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForge::onEntityJoin);
        MinecraftForge.EVENT_BUS.addListener(PrometheusForge::onServerStarted);
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        Prometheus.postInit();
    }

    private static void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    private static void onChatMessage(ServerChatEvent event) {
        if (!MuteHandler.canMessageGoThrough(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        PermissionEvents.onEntityJoin(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer player) {
            HeadingEvents.onJoin(player);
            NicknameEvents.onJoin(player);
        }
    }

    private static void onServerStarted(ServerStartedEvent event) {
        Prometheus.onServerStarted(event.getServer());
    }
}