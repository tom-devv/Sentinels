package dev.tom.sentinels.launchable.impl.flares;

import dev.tom.sentinels.launchable.Gravity;
import dev.tom.sentinels.launchable.ItemSupplier;
import dev.tom.sentinels.launchable.Velocity;
import dev.tom.sentinels.launchable.items.FieldInfo;
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
    public Component nameComponent() {
        return MiniMessage.miniMessage().deserialize("<green>Flare</green>");
    }

    @Override
    public List<Component> prefixLoreComponent() {
        MiniMessage mm = MiniMessage.miniMessage();
        String string = """
                <white>Launch this flare<newline>
                to summon a rally of <blue>allays</blue>.<newline>
                These helpful <blue>allays</blue> will help<newline>
                to repair your barriers until they are killed!<newline>
                </white>
                """;
        return List.of(
                mm.deserialize(
                        string
                )
        );
    }

    @Override
    public Material material() {
        return Material.REDSTONE_BLOCK;
    }
}
