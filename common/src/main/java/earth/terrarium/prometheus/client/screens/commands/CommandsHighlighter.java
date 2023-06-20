package earth.terrarium.prometheus.client.screens.commands;

import earth.terrarium.prometheus.client.screens.widgets.editor.TextHighlighter;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class CommandsHighlighter implements TextHighlighter {

    private final Object2IntMap<Pattern> colors;
    private final CommandEditorTheme.Theme theme;

    public CommandsHighlighter(CommandEditorTheme.Theme theme) {
        this.colors = CommandEditorTheme.getSyntaxHighlighting();
        this.theme = theme;
    }

    @Override
    public Component highlight(String text) {
        List<Component> components = new ArrayList<>();
        Pattern[] patterns = colors.keySet().toArray(new Pattern[0]);
        forEach(
            text,
            out -> {
                if (components.isEmpty()) {
                    String[] split = out.split(" ");
                    components.add(Component.literal(split[0]).withStyle(style -> style.withColor(theme.firstWord())));
                    String rest = out.substring(split[0].length());
                    if (!rest.isEmpty()) {
                        components.add(Component.literal(rest).withStyle(style -> style.withColor(theme.text())));
                    }
                } else {
                    components.add(Component.literal(out).withStyle(style -> style.withColor(theme.text())));
                }
            },
            (pattern, matched) -> {
                int color = colors.getInt(pattern);
                components.add(Component.literal(matched).withStyle(style -> style.withColor(color)));
            },
            patterns
        );
        MutableComponent component = Component.empty();
        for (Component c : components) component.append(c);
        return component;
    }

    private static void forEach(String input, Consumer<String> normal, BiConsumer<Pattern, String> matched, Pattern... patterns) {
        if (patterns.length == 0) {
            normal.accept(input);
            return;
        }
        while (!input.isEmpty()) {
            boolean found = false;
            for (Pattern pattern : patterns) {
                var matcher = pattern.matcher(input);
                if (matcher.find()) {
                    if (matcher.start() > 0) {
                        forEach(input.substring(0, matcher.start()), normal, matched, patterns);
                    }
                    matched.accept(pattern, input.substring(matcher.start(), matcher.end()));
                    input = input.substring(matcher.end());
                    found = true;
                    break;
                }
            }
            if (!found) {
                normal.accept(input);
                input = "";
            }
        }
    }
}
