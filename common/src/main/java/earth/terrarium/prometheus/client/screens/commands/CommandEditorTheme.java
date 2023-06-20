package earth.terrarium.prometheus.client.screens.commands;

import com.google.gson.JsonObject;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import earth.terrarium.prometheus.Prometheus;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;

import java.util.regex.Pattern;

public class CommandEditorTheme {

    private static final ResourceLocation SYNTAX = new ResourceLocation(Prometheus.MOD_ID, "editor_theme.json");

    public static Theme getTextTheme() {
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(SYNTAX).orElse(null);
        if (resource != null) {
            try {
                String json = IOUtils.toString(resource.openAsReader());
                JsonObject object = Constants.GSON.fromJson(json, JsonObject.class);
                int defaultColor = object.get("default").getAsInt();
                int firstWordColor = object.get("first_word").getAsInt();
                int lineNumsColor = object.get("line_nums").getAsInt();
                int cursorColor = object.get("cursor").getAsInt();
                return new Theme(lineNumsColor, cursorColor, defaultColor, firstWordColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Theme(0x404040, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF);
    }

    public static Object2IntMap<Pattern> getSyntaxHighlighting() {
        Object2IntMap<Pattern> colors = new Object2IntLinkedOpenHashMap<>();
        Resource resource = Minecraft.getInstance().getResourceManager().getResource(SYNTAX).orElse(null);
        if (resource != null) {
            try {
                String json = IOUtils.toString(resource.openAsReader());
                JsonObject object = Constants.GSON.fromJson(json, JsonObject.class).getAsJsonObject("syntax");
                object.entrySet().forEach(entry -> {
                    String key = entry.getKey();
                    Pattern pattern = Pattern.compile(key);
                    colors.put(pattern, entry.getValue().getAsInt());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return colors;
    }

    public record Theme(int lineNums, int cursor, int text, int firstWord) {}
}
