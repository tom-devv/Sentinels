package dev.tom.sentinels.items.rally;

import dev.tom.sentinels.ai.GhastRallyGoal;
import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.items.Item;
import dev.tom.sentinels.items.ItemListener;
import dev.tom.sentinels.items.ItemSupplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.Random;

public class Flag extends Item<FlagAttributes> {


    public Flag(@NotNull ItemStack item, @NotNull Class<FlagAttributes> type) {
        super(item, type);
    }

    private static class FlagListeners implements ItemListener<Flag> {

        @EventHandler
        public void flagPlace(BlockPlaceEvent e) {
            ItemStack item = e.getItemInHand();
            Optional<FlagAttributes> optional;
            if((optional = SentinelDataWrapper.getInstance().loadPDC(item.getItemMeta(), FlagAttributes.class)).isEmpty()) return;
            FlagAttributes attributes = optional.get();

            System.out.println("Flag placed");

            Player player = e.getPlayer();


            int rand = new Random().nextInt(50);
            Location ghastLocation = player.getLocation().clone().add(rand, rand, rand);
            HappyGhast happyGhast = player.getWorld().spawn(ghastLocation, HappyGhast.class, ghast -> {
                ghast.setInvulnerable(true);
            });
            Bukkit.getMobGoals().addGoal(happyGhast, 0, new GhastRallyGoal(happyGhast, e.getBlock().getLocation()));

        }
    }

}
