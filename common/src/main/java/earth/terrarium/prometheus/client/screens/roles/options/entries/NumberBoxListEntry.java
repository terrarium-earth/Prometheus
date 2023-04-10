package earth.terrarium.prometheus.client.screens.roles.options.entries;

import net.minecraft.network.chat.Component;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class NumberBoxListEntry extends TextBoxListEntry {

    public NumberBoxListEntry(int amount, boolean decimals, Component component) {
        super(String.valueOf(amount), 1000, component, text -> {
            try {
                if (decimals) {
                    Double.parseDouble(text);
                } else {
                    Integer.parseInt(text);
                }
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    public OptionalDouble getDoubleValue() {
        try {
            return OptionalDouble.of(Double.parseDouble(getText()));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }

    public OptionalInt getIntValue() {
        try {
            return OptionalInt.of(Integer.parseInt(getText()));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
