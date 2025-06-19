package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.projectiles.Gravity;
import dev.tom.sentinels.projectiles.ItemSupplier;
import dev.tom.sentinels.projectiles.Velocity;
import dev.tom.sentinels.projectiles.items.DisplayInfo;
import org.bukkit.Material;

import java.util.UUID;

public record FlareAttributes(
        @DisplayInfo(ignore = true) UUID uuid,
        boolean gravity,
        double healing,
        double mobHealth,
        int mobCount,
        int searchRadius,
        double velocity
) implements Gravity, Velocity, java.io.Serializable, ItemSupplier {

    @Override
    public boolean gravity() {
        return this.gravity;
    }

    @Override
    public double velocity() {
        return this.velocity;
    }

    @Override
    public String getName() {
        return "Flare";
    }

    @Override
    public Material getMaterial() {
        return Material.REDSTONE_BLOCK;
    }
}
