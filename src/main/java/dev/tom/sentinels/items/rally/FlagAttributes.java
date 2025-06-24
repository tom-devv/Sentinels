package dev.tom.sentinels.items.rally;

import dev.tom.sentinels.items.ItemSupplier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;

import java.io.Serializable;

public record FlagAttributes() implements Serializable, ItemSupplier {


    @Override
    public Component nameComponent() {
        return MiniMessage.miniMessage().deserialize("<green><bold>Rallying Flag");
    }

    @Override
    public Material material() {
        return Material.GREEN_BANNER;
    }
}
