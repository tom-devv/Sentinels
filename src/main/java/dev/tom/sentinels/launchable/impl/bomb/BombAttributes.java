package dev.tom.sentinels.launchable.impl.bomb;

import dev.tom.sentinels.launchable.ItemSupplier;
import dev.tom.sentinels.launchable.attributes.Gravity;
import dev.tom.sentinels.launchable.attributes.Knockback;
import dev.tom.sentinels.launchable.attributes.Velocity;
import dev.tom.sentinels.items.FieldInfo;
import dev.tom.sentinels.utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record BombAttributes(
        @FieldInfo(ignore = true) UUID uuid,
        boolean gravity,
        @FieldInfo(name = "speed", unit = "m/s") double velocity,
        @FieldInfo(name = "Total Damage") double damage,
        @FieldInfo(unit = "m") double radius,
        @FieldInfo(unit = "m/s") double knockback,
        int explosions
) implements Gravity, Velocity, Knockback, Serializable, ItemSupplier {

    private static final String name = "<gradient:#E7663D:#E73D3D>Atom Bomb</gradient>";

    @Override
    public Component nameComponent() {
        return MiniMessage.miniMessage().deserialize(name);
    }

    @Override
    public List<Component> prefixLoreComponent() {
        return TextUtil.asComponent(
                "<white>Launch this " + name + " to decrease the shield's",
                "<white><bold>total</bold> health by: <red>" + damage + "</red> points!</white>",
                ""
        );
    }

    @Override
    public Material material() {
        return Material.MAGMA_BLOCK;
    }
}
