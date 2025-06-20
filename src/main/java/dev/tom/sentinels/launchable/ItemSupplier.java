package dev.tom.sentinels.launchable;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;


public interface ItemSupplier {

    Component nameComponent();

    default List<Component> prefixLoreComponent(){
        return List.of(Component.text(""));
    }

    Material material();


}
