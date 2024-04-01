package earth.terrarium.prometheus.common.commands.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.teamresourceful.resourcefullib.common.utils.CommonUtils;
import earth.terrarium.prometheus.api.roles.RoleApi;
import earth.terrarium.prometheus.common.constants.ConstantComponents;
import earth.terrarium.prometheus.common.handlers.cooldowns.CooldownHandler;
import earth.terrarium.prometheus.common.roles.TeleportOptions;
import earth.terrarium.prometheus.common.utils.ModUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class RtpCommand {

    private static final int MAX_TRIES = 10;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                if (player.level().dimensionType().hasCeiling()) {
                    context.getSource().sendFailure(ConstantComponents.FAILED_WITH_CEILING);
                    return 0;
                }
                if (CooldownHandler.hasCooldown(player, "rtp")) {
                    int timeLeft = (int) ((CooldownHandler.getCooldown(player, "rtp") - System.currentTimeMillis()) / 1000);
                    context.getSource().sendFailure(CommonUtils.serverTranslatable("prometheus.rtp.failed_cooldown", timeLeft));
                    return 0;
                }
                TeleportOptions options = RoleApi.API.getNonNullOption(player, TeleportOptions.SERIALIZER);
                if (tp(player, options.rtpDistance())) {
                    CooldownHandler.setCooldown(player, "rtp", options.rtpCooldown());
                    return 1;
                }
                return 0;
            }));
    }

    private static boolean tp(ServerPlayer player, int distance) {
        BlockPos pos = tp(player.blockPosition(), player, distance, 0);
        if (pos == null) {
            player.sendSystemMessage(ConstantComponents.FAILED_MAX_TRIES);
            return false;
        }

        ModUtils.teleport(player, player.serverLevel(), pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.sendSystemMessage(ConstantComponents.TELEPORTED);
        return true;
    }

    public static boolean isSafe(Player player, BlockPos pos) {
        int maxHeight = (int) Math.min(3, Math.ceil(player.getBbHeight()) + 1);
        for (int i = 0; i < maxHeight; i++) {
            BlockState state = player.level().getBlockState(pos.above(i));
            if (!state.isAir()) {
                return false;
            }
        }
        return player.level().getBlockState(pos.below()).entityCanStandOn(player.level(), pos.below(), player);
    }

    public static BlockPos tp(BlockPos location, ServerPlayer player, int distance, int tries) {
        if (tries > MAX_TRIES) return null;
        Level level = player.level();

        final int min = distance / 4;
        final int max = distance - min;

        var theta = player.getRandom().nextDouble() * 2 * Math.PI;
        int r = min + player.getRandom().nextInt(max);
        int x = Math.round(r * (float) Math.cos(theta));
        int z = Math.round(r * (float) Math.sin(theta));

        if (!level.getWorldBorder().isWithinBounds(x, z) || level.getBiome(new BlockPos(x, level.getSeaLevel(), z)).is(BiomeTags.IS_OCEAN)) {
            return tp(location, player, distance, tries + 1);
        }

        level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));

        BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);

        if (!isSafe(player, pos)) {
            return tp(location, player, distance, tries + 1);
        }
        return pos;
    }
}
