package earth.terrarium.prometheus.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import earth.terrarium.prometheus.common.commands.admin.*;
import earth.terrarium.prometheus.common.commands.cheating.FeedCommand;
import earth.terrarium.prometheus.common.commands.cheating.FlyCommand;
import earth.terrarium.prometheus.common.commands.cheating.GodModeCommand;
import earth.terrarium.prometheus.common.commands.cheating.HealCommand;
import earth.terrarium.prometheus.common.commands.roles.MemberCommand;
import earth.terrarium.prometheus.common.commands.roles.PromotionCommand;
import earth.terrarium.prometheus.common.commands.roles.RolesCommand;
import earth.terrarium.prometheus.common.commands.utilities.*;
import earth.terrarium.prometheus.common.network.NetworkHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ModCommands {

    private static final SimpleCommandExceptionType MOD_NOT_INSTALLED = new SimpleCommandExceptionType(
        Component.translatableEscape("commands.prometheus.mod_not_installed")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, Commands.CommandSelection environment) {
        HealCommand.register(dispatcher);
        FeedCommand.register(dispatcher);
        FlyCommand.register(dispatcher);
        HatCommand.register(dispatcher);
        InvseeCommand.register(dispatcher);
        GodModeCommand.register(dispatcher);
        MuteCommand.register(dispatcher);
        WarpCommand.register(dispatcher);
        TpaCommand.register(dispatcher);
        HomeCommand.register(dispatcher);
        TpCommand.register(dispatcher);
        RtpCommand.register(dispatcher);
        HeadingCommand.register(dispatcher);
        TpToCommand.register(dispatcher);
        NicknameCommand.register(dispatcher);
        OfflineCommand.register(dispatcher);
        PromotionCommand.register(dispatcher, ctx);
        MemberCommand.register(dispatcher);
        RolesCommand.register(dispatcher);
    }

    public static void checkPacket(ServerPlayer player, PacketType<?> type) throws CommandSyntaxException {
        if (!NetworkHandler.CHANNEL.canSendToPlayer(player, type)) {
            throw MOD_NOT_INSTALLED.create();
        }
    }
}
