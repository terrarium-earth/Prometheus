package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Optional;

public class TpCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("back")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                player.getLastDeathLocation()
                    .ifPresent(pos -> {
                        player.setLastDeathLocation(Optional.empty());
                        ServerLevel level = player.server.getLevel(pos.dimension());
                        if (level == null) {
                            player.sendSystemMessage(ConstantComponents.NO_DIMENSION);
                            return;
                        }
                        player.teleportTo(level, pos.pos().getX(), pos.pos().getY(), pos.pos().getZ(), player.getYRot(), player.getXRot());
                    });
                return 1;
            }));
        dispatcher.register(Commands.literal("spawn")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                BlockPos pos = player.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, player.level.getSharedSpawnPos());
                player.teleportTo(player.server.overworld(), pos.getX(), pos.getY(), pos.getZ(), player.getYRot(), player.getXRot());
                return 1;
            }));
    }
}
