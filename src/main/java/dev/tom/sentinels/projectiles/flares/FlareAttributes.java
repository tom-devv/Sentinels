package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.projectiles.Gravity;
import dev.tom.sentinels.projectiles.Velocity;

import java.util.UUID;

public record FlareAttributes(
        UUID uuid,
        boolean gravity,
        double healingPerTick,
        double mobHealth,
        int mobCount,
        int searchRadius,
        double velocity
) implements Gravity, Velocity, java.io.Serializable {

    @Override
    public boolean gravity() {
        return this.gravity;
    }

    @Override
    public double velocity() {
        return this.velocity;
    }
}
