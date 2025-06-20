package dev.tom.sentinels.launchable.impl.shells;

import dev.tom.sentinels.launchable.Gravity;
import dev.tom.sentinels.launchable.ItemSupplier;
import dev.tom.sentinels.launchable.Velocity;
import dev.tom.sentinels.launchable.items.FieldInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.UUID;

public record ShellAttributes(
        @FieldInfo(ignore = true) UUID uuid,
        double damage,
        @FieldInfo(unit = "m/s") double speed,
        boolean gravity,
        @FieldInfo(unit = "m") double radius,
        @FieldInfo(name = "Recoil", unit = "m/s") double knockback
) implements Gravity, Serializable, Velocity, ItemSupplier {

    @Override
    public boolean gravity() {
        return this.gravity;
    }

    @Override
    public Component nameComponent() {
        return MiniMessage.miniMessage().deserialize("<red>Shell</red>");
    }

    @Override
    public Material material() {
        return Material.RED_STAINED_GLASS;
    }

    @Override
    public double velocity() {
        return this.speed;
    }
}
