package earth.terrarium.prometheus.common.handlers.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class DynamicCommand {

    public static final Pattern CUSTOM_PARAMETER_PATTERN = Pattern.compile("\\$\\{\\[(\\d+(?:-(?:\\d+|\\*))?)]}");
    public static final Pattern USER_PARAMETER_PATTERN = Pattern.compile("\\$\\{user}");

    public static int execute(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source, String username, String[] args, List<String> lines) {
        List<ParseResults<CommandSourceStack>> results = fromLines(dispatcher, source, parseLines(lines, username, args));

        try {
            for (ParseResults<CommandSourceStack> result : results) {
                dispatcher.execute(result);
            }
        } catch (Exception exception) {
            if (exception instanceof DynamicCommandException) throw new DynamicCommandException(exception.getMessage());
            else throw new DynamicCommandException("An error occurred while executing the command");
        }

        return results.size();
    }

    private static List<String> parseLines(List<String> lines, String user, String... args) {
        List<String> parsedLines = new ArrayList<>();
        for (String line : lines) {
            line = USER_PARAMETER_PATTERN.matcher(line).replaceAll(Objects.requireNonNullElse(user, "Unknown"));
            var matcher = CUSTOM_PARAMETER_PATTERN.matcher(line);
            parsedLines.add(matcher.replaceAll(result -> String.join(" ", getArgs(args, result.group(1)))));
        }
        return parsedLines;
    }

    private static List<String> getArgs(String[] args, String matcher) {
        try {
            IntIntPair range = getRange(args, matcher);
            if (range.firstInt() >= args.length || range.secondInt() >= args.length)
                throw new DynamicCommandException("Missing arguments");
            if (range.firstInt() > range.secondInt()) throw new DynamicCommandException("Invalid range");
            if (args.length - 1 > range.secondInt()) throw new DynamicCommandException("Too many arguments");
            return new ArrayList<>(Arrays.asList(args).subList(range.firstInt(), range.secondInt() + 1));
        } catch (NumberFormatException e) {
            throw new DynamicCommandException("Failed to parse range");
        } catch (IndexOutOfBoundsException e) {
            throw new DynamicCommandException("Missing Arguments");
        }
    }

    private static IntIntPair getRange(String[] args, String matcher) {
        String[] split = matcher.split("-");
        int start = 0;
        int end = 0;
        if (split.length > 0) {
            start = Integer.parseInt(split[0]);
            end = start;
        }
        if (split.length > 1) {
            end = split[1].equals("*") ? args.length - 1 : Integer.parseInt(split[1]);
        }
        return IntIntPair.of(start, end);
    }

    private static List<ParseResults<CommandSourceStack>> fromLines(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source, List<String> lines) {
        List<ParseResults<CommandSourceStack>> results = new ArrayList<>(lines.size());

        for (String s : lines) {
            StringReader reader = new StringReader(s.trim());
            if (reader.canRead() && reader.peek() != '#') {
                try {
                    ParseResults<CommandSourceStack> result = dispatcher.parse(reader, source);
                    if (result.getReader().canRead()) {
                        throw Objects.requireNonNull(Commands.getParseException(result));
                    }

                    results.add(result);
                } catch (Exception exception) {
                    throw new DynamicCommandException("Whilst parsing command line " + s.trim() + ": " + exception.getMessage());
                }
            }
        }

        return results;
    }
}
