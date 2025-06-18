package dev.tom.sentinels.projectiles.shells;

import dev.tom.sentinels.projectiles.Gravity;
import dev.tom.sentinels.projectiles.ItemSupplier;
import org.bukkit.Material;

import java.io.Serializable;

public record ShellAttributes(
        double damage,
        boolean gravity,
        double radius
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
