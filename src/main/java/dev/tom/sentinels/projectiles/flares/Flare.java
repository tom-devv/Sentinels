package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.projectiles.AttributeLaunchable;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;


public class Flare extends AttributeLaunchable<FlareAttributes> implements Listener {

    public Flare(ItemStack item) {
        super(item, Material.REDSTONE_BLOCK.createBlockData(), FlareAttributes.class);
    }



}
