package earth.terrarium.prometheus.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import earth.terrarium.prometheus.common.commands.admin.InvseeCommand;
import earth.terrarium.prometheus.common.commands.admin.MuteCommand;
import earth.terrarium.prometheus.common.commands.admin.TpToCommand;
import earth.terrarium.prometheus.common.commands.admin.WarpCommand;
import earth.terrarium.prometheus.common.commands.cheating.FeedCommand;
import earth.terrarium.prometheus.common.commands.cheating.FlyCommand;
import earth.terrarium.prometheus.common.commands.cheating.GodModeCommand;
import earth.terrarium.prometheus.common.commands.cheating.HealCommand;
import earth.terrarium.prometheus.common.commands.testing.PermissionCommand;
import earth.terrarium.prometheus.common.commands.utilities.HeadingCommand;
import earth.terrarium.prometheus.common.commands.utilities.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {

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
        PermissionCommand.register(dispatcher);
    }
}
