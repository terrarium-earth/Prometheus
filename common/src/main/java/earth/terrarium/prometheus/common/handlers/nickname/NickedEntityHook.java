package earth.terrarium.prometheus.common.handlers.nickname;

import net.minecraft.network.chat.Component;

public interface NickedEntityHook {

    void prometheus$setNickname(Component nickname);

    Component prometheus$getNickname();
}
