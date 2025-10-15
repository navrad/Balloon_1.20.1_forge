package heighteffect;

import net.minecraftforge.eventbus.api.Event;
import net.minecraft.world.entity.player.Player;

public class HeightEffectImmunityEvent extends Event {
    private final Player player;
    private final int immunityTime;

    public HeightEffectImmunityEvent(Player player, int immunityTime) {
        this.player = player;
        this.immunityTime = immunityTime;
    }

    public Player getPlayer() {
        return player;
    }

    public int getImmunityTime() {
        return immunityTime;
    }
}