package earth.terrarium.prometheus.client.utils;

import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;

public class ClientUtils {

    private static final Char2CharMap SMALL_NUMBERS = new Char2CharOpenHashMap();

    static {
        SMALL_NUMBERS.put('0', '₀');
        SMALL_NUMBERS.put('1', '₁');
        SMALL_NUMBERS.put('2', '₂');
        SMALL_NUMBERS.put('3', '₃');
        SMALL_NUMBERS.put('4', '₄');
        SMALL_NUMBERS.put('5', '₅');
        SMALL_NUMBERS.put('6', '₆');
        SMALL_NUMBERS.put('7', '₇');
        SMALL_NUMBERS.put('8', '₈');
        SMALL_NUMBERS.put('9', '₉');
    }

    public static String getSmallNumber(int num) {
        String normal = String.valueOf(num);
        StringBuilder builder = new StringBuilder(normal.length());
        for (char c : String.valueOf(num).toCharArray()) {
            builder.append(SMALL_NUMBERS.getOrDefault(c, c));
        }
        return builder.toString();
    }
}
