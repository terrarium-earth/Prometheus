package earth.terrarium.prometheus.fabric;

import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.common.commands.ModCommands;
import earth.terrarium.prometheus.common.handlers.MuteHandler;
import earth.terrarium.prometheus.common.handlers.permission.PermissionEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

public class PrometheusFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Prometheus.init();
        CommandRegistrationCallback.EVENT.register(ModCommands::register);
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((msg, player, params) -> MuteHandler.canMessageGoThrough(player));
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> PermissionEvents.onEntityJoin(entity));
    }
}