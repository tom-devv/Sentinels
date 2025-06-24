package dev.tom.sentinels.launchables.impl.flares;

import dev.tom.sentinels.launchables.attributes.Gravity;
import dev.tom.sentinels.items.ItemSupplier;
import dev.tom.sentinels.launchables.attributes.Velocity;
import dev.tom.sentinels.items.FieldInfo;
import dev.tom.sentinels.utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public record FlareAttributes(
        @FieldInfo(ignore = true) UUID uuid,
        boolean gravity,
        double healing,
        @FieldInfo(name = "Allay Health") double mobHealth,
        @FieldInfo(name = "Allays") int mobCount,
        int searchRadius,
        @FieldInfo(name = "speed", unit = "m/s") double velocity
) implements Gravity, Velocity, java.io.Serializable, ItemSupplier {
    @Override
    public Component nameComponent() {
        return MiniMessage.miniMessage().deserialize("<green>Flare</green>");
    }

    @Override
    public List<Component> prefixLoreComponent() {
        return TextUtil.asComponent(
                "<white>Launch this flare</white>",
                "<white>to summon a really of <blue>allays</blue></white>",
                "<white>These helpful <blue>allays</blue> will <green>repair</green></white>",
                "<white>your barriers until they are fully restored</white>",
                ""
        );
    }

    @Override
    public Material material() {
        return Material.REDSTONE_BLOCK;
    }
}
