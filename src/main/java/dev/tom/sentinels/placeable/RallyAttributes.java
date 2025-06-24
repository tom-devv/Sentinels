package dev.tom.sentinels.placeable;

import dev.tom.sentinels.launchable.ItemSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

public record RallyAttributes() implements ItemSupplier {


    @Override
    public Component nameComponent() {
        return MiniMessage.miniMessage().deserialize("<blue>Rally Point");
    }

    @Override
    public Material material() {
        return Material.RED_CANDLE;
    }
}
