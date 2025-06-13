package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.projectiles.Gravity;
import org.bukkit.entity.Player;


public record FlareAttributes(
        Player player,
        boolean gravity,
        double healingPerTick,
        double mobHealth,
        int mobCount,
        int searchRadius
) implements Gravity, java.io.Serializable {

    @Override
    public boolean gravity() {
        return this.gravity;
    }
}
