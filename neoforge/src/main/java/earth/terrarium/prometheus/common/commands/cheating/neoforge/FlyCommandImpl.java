package earth.terrarium.prometheus.common.commands.cheating.neoforge;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import earth.terrarium.prometheus.Prometheus;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForgeMod;

public class FlyCommandImpl {

    private static final ResourceLocation FLY_ABILITY = Prometheus.id("fly_ability");
    private static final Multimap<Holder<Attribute>, AttributeModifier> FLIGHT_ATTRIBUTES = getFlightAttributes();

    public static void setCanFly(Entity entity) {
        if (entity instanceof Player player) {
            var attributes = player.getAttributes();
            if (attributes.hasModifier(NeoForgeMod.CREATIVE_FLIGHT, FLY_ABILITY)) {
                attributes.removeAttributeModifiers(FLIGHT_ATTRIBUTES);
            } else {
                attributes.addTransientAttributeModifiers(FLIGHT_ATTRIBUTES);
            }
        }
    }

    private static Multimap<Holder<Attribute>, AttributeModifier> getFlightAttributes() {
        ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
            NeoForgeMod.CREATIVE_FLIGHT,
            new AttributeModifier(FLY_ABILITY, 1.0D, AttributeModifier.Operation.ADD_VALUE)
        );
        return builder.build();
    }
}
