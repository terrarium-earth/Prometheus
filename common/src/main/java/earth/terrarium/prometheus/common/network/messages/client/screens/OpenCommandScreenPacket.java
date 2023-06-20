package earth.terrarium.prometheus.common.network.messages.client.screens;

import com.teamresourceful.resourcefullib.common.networking.base.Packet;
import com.teamresourceful.resourcefullib.common.networking.base.PacketContext;
import com.teamresourceful.resourcefullib.common.networking.base.PacketHandler;
import earth.terrarium.prometheus.Prometheus;
import earth.terrarium.prometheus.client.screens.commands.EditCommandScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public record OpenCommandScreenPacket(String command, List<String> content,
                                      Collection<String> commands) implements Packet<OpenCommandScreenPacket> {

    public static final ResourceLocation ID = new ResourceLocation(Prometheus.MOD_ID, "open_command_screen");
    public static final PacketHandler<OpenCommandScreenPacket> HANDLER = new Handler();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public PacketHandler<OpenCommandScreenPacket> getHandler() {
        return HANDLER;
    }

    private static class Handler implements PacketHandler<OpenCommandScreenPacket> {

        @Override
        public void encode(OpenCommandScreenPacket message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.command);
            buffer.writeCollection(message.content, FriendlyByteBuf::writeUtf);
            buffer.writeCollection(message.commands, FriendlyByteBuf::writeUtf);
        }

        @Override
        public OpenCommandScreenPacket decode(FriendlyByteBuf buffer) {
            String command = buffer.readUtf();
            List<String> content = buffer.readList(FriendlyByteBuf::readUtf);
            Set<String> commands = new LinkedHashSet<>();
            commands.add(command);
            commands.addAll(buffer.readList(FriendlyByteBuf::readUtf));
            return new OpenCommandScreenPacket(command, content, commands);
        }

        @Override
        public PacketContext handle(OpenCommandScreenPacket message) {
            return (player, level) ->
                Minecraft.getInstance().setScreen(new EditCommandScreen(message.commands(), message.content(), message.command()));
        }
    }
}
