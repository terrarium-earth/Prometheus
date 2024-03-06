package earth.terrarium.prometheus.neoforge;

import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.commands.ModCommands;
import earth.terrarium.prometheus.common.handlers.MuteHandler;
import earth.terrarium.prometheus.common.handlers.heading.HeadingEvents;
import earth.terrarium.prometheus.common.handlers.nickname.NicknameEvents;
import earth.terrarium.prometheus.common.handlers.permission.CommandPermissionHandler;
import earth.terrarium.prometheus.common.handlers.permission.PermissionEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.handler.DefaultPermissionHandler;
import net.neoforged.neoforge.server.permission.handler.IPermissionHandler;
import net.neoforged.neoforge.server.permission.handler.IPermissionHandlerFactory;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;

import java.util.Collection;
import java.util.function.Supplier;

@Mod(Prometheus.MOD_ID)
public class PrometheusNeoForge {

    private static Supplier<IPermissionHandlerFactory> parentPermissionFactory = null;

    public PrometheusNeoForge(IEventBus bus) {
        Prometheus.init();
        if (FMLEnvironment.dist.isClient()) {
            PrometheusNeoForgeClient.init(bus);
        }
        bus.addListener(PrometheusNeoForge::onCommonSetup);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::registerCommands);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onChatMessage);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onServerStarted);
        NeoForge.EVENT_BUS.addListener(PrometheusNeoForge::onServerTick);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, PrometheusNeoForge::onAddHandlers);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PrometheusNeoForgeConfig.SERVER_CONFIG);
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
            CommandPermissionHandler.onJoin(player);
        }
    }

    private static void onServerStarted(ServerStartedEvent event) {
        Prometheus.onServerStarted(event.getServer());
    }

    private static void onServerTick(TickEvent.ServerTickEvent event) {
        Prometheus.onServerTick(event.getServer());
    }

    private static void onAddHandlers(PermissionGatherEvent.Handler event) {
        parentPermissionFactory = () -> {
            ResourceLocation id = ResourceLocation.tryParse(PrometheusNeoForgeConfig.PARENT_PERMISSION_HANDLER.get());
            var handlers = event.getAvailablePermissionHandlerFactories();
            if (id == null || !handlers.containsKey(id)) {
                return DefaultPermissionHandler::new;
            }
            return event.getAvailablePermissionHandlerFactories().get(id);
        };

        event.addPermissionHandler(PrometheusPermissionHandler.ID, PrometheusPermissionHandler::new);
    }

    public static IPermissionHandler getHandler(Collection<PermissionNode<?>> permissions) {
        if (parentPermissionFactory == null) {
            return new DefaultPermissionHandler(permissions);
        }
        return parentPermissionFactory.get().create(permissions);
    }

}