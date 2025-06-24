package dev.tom.sentinels.launchable.impl.shells;

import dev.tom.sentinels.launchable.attributes.Gravity;
import dev.tom.sentinels.launchable.ItemSupplier;
import dev.tom.sentinels.launchable.attributes.Knockback;
import dev.tom.sentinels.launchable.attributes.Velocity;
import dev.tom.sentinels.items.FieldInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.UUID;

public record ShellAttributes(
        @FieldInfo(ignore = true) UUID uuid,
        double damage,
        @FieldInfo(name = "speed", unit = "m/s") double velocity,
        boolean gravity,
        @FieldInfo(unit = "m") double radius,
        @FieldInfo(name = "Recoil", unit = "m/s") double knockback
) implements Gravity, Serializable, Velocity, Knockback, ItemSupplier {

    @Override
    public Component nameComponent() {
        return MiniMessage.miniMessage().deserialize("<red>Shell</red>");
    }

    @Override
    public Material material() {
        return Material.RED_STAINED_GLASS;
    }
}
