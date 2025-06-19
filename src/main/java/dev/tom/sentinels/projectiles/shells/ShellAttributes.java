package dev.tom.sentinels.projectiles.shells;

import dev.tom.sentinels.projectiles.Gravity;
import dev.tom.sentinels.projectiles.ItemSupplier;
import dev.tom.sentinels.projectiles.items.DisplayInfo;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.UUID;

public record ShellAttributes(
        @DisplayInfo(ignore = true) UUID uuid,
        double damage,
        boolean gravity,
        @DisplayInfo(unit = "m") double radius
) implements Gravity, Serializable, ItemSupplier {

    @Override
    public boolean gravity() {
        return this.gravity;
    }

    @Override
    public String getName() {
        return "Shell";
    }

    @Override
    public Material getMaterial() {
        return Material.RED_STAINED_GLASS;
    }
}
