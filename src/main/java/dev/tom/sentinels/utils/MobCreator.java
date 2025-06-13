package dev.tom.sentinels.utils;

import dev.tom.sentinels.ai.AllayRepairGoal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class MobCreator {
    public static Entity createMob(Location loc, Location target, Location fixBlock) {
        Allay allay = loc.getWorld().spawn(loc, Allay.class, mob -> {
            mob.setCanPickupItems(false);
            mob.setCollidable(true);
            mob.getEquipment().setItemInMainHand(new ItemStack(Material.GLASS));
        });
        Bukkit.getMobGoals().addGoal(allay, 0, new AllayRepairGoal(allay, target, fixBlock));
        return allay;
    }
}
