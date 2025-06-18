package dev.tom.sentinels.utils;

import dev.tom.sentinels.ai.AllayRepairGoal;
import dev.tom.sentinels.projectiles.flares.FlareAttributes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Allay;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class MobCreator {
    public static Set<Allay> createAllays(Location loc, Location hitBlock, FlareAttributes attributes) {
        Set<Allay> allays = new HashSet<>();
        for (int i = 0; i < attributes.mobCount(); i++) {
            Allay allay = loc.getWorld().spawn(loc, Allay.class, mob -> {
                mob.setCanPickupItems(false);
                mob.setCollidable(true);
                mob.getEquipment().setItemInMainHand(new ItemStack(Material.GLASS));
            });
            allays.add(allay);
            Bukkit.getMobGoals().addGoal(allay, 0, new AllayRepairGoal(allay, hitBlock, attributes.searchRadius(), attributes.healing()));
        }
        return allays;
    }
}
