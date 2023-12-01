package earth.terrarium.prometheus.neoforge;

import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.commands.ModCommands;
import earth.terrarium.prometheus.common.handlers.MuteHandler;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEvents;
import earth.terrarium.prometheus.common.handlers.nickname.NicknameEvents;
import earth.terrarium.prometheus.common.handlers.permission.PermissionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(Prometheus.MOD_ID)
public class PrometheusNeoForge {

    public PrometheusNeoForge() {
        Prometheus.init();
        if (FMLEnvironment.dist.isClient()) {
            PrometheusNeoForgeClient.init();
        }
        FMLJavaModLoadingContext.get().getModEventBus().addListener(PrometheusNeoForge::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::registerCommands);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onChatMessage);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onServerStarted);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onServerTick);
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

    private static void onServerTick(TickEvent.ServerTickEvent event) {
        Prometheus.onServerTick(event.getServer());
    }
}