package earth.terrarium.prometheus.common.commands.admin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.CustomPlayerDataHandler;
import earth.terrarium.prometheus.mixin.common.accessors.PlayerListAccessor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;

public class OfflineCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("offlinetp")
            .requires(context -> context.hasPermission(2))
            .then(Commands.argument("player", GameProfileArgument.gameProfile())
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                    .executes(context -> {
                        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
                        if (profiles.size() != 1) {
                            context.getSource().sendFailure(ConstantComponents.MULTIPLE_PLAYERS);
                            return 0;
                        }
                        MinecraftServer server = context.getSource().getServer();
                        GameProfile profile = profiles.iterator().next();
                        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");

                        if (server.getPlayerList().getPlayer(profile.getId()) != null) {
                            context.getSource().sendFailure(ConstantComponents.PLAYER_ONLINE);
                            return 0;
                        }

                        CustomPlayerDataHandler handler = (CustomPlayerDataHandler) ((PlayerListAccessor) server.getPlayerList()).getPlayerIo();
                        handler.prometheus$edit(profile.getId(), tag -> {
                            tag.put("Pos", newDoubleList(pos.getX(), pos.getY(), pos.getZ()));
                            tag.putString("Dimension", context.getSource().getLevel().dimension().location().toString());
                        });
                        context.getSource().sendSuccess(() -> Component.translatable("commands.teleport.success.location.single", profile.getName(), pos.getX(), pos.getY(), pos.getZ()), true);
                        return 1;
                    })
                )
            ));
    }

    protected static ListTag newDoubleList(double... numbers) {
        ListTag listTag = new ListTag();
        for (double d : numbers) {
            listTag.add(DoubleTag.valueOf(d));
        }

        return listTag;
    }
}
