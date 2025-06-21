package dev.tom.sentinels.launchable.impl.flares;

import dev.tom.sentinels.ai.AllayRepairGoal;
import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.launchable.AbstractLaunchable;
import dev.tom.sentinels.launchable.LaunchableListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;


public class Flare extends AbstractLaunchable<FlareAttributes>  {

    public Flare(ItemStack item) {
        super(item, Material.REDSTONE_BLOCK.createBlockData(), FlareAttributes.class);
    }

    /**
     * Create allays with goals to repair barriers
     * @param direction location adjacent to the hit block (outside the barrier)
     * @param hitBlock the block the flare collided with
     * @param attributes
     * @return
     */
    protected static Set<Allay> createAllays(Vector direction, Location hitBlock, FlareAttributes attributes) {
        Set<Allay> allays = new HashSet<>();

        // TODO this should be fixed with better collisions
        double distanceFromBarrier = 5;
        direction = direction.normalize().multiply(distanceFromBarrier);
        Location spawn = hitBlock.clone().add(direction);
        for (int i = 0; i < attributes.mobCount(); i++) {
            Allay allay = spawn.getWorld().spawn(spawn, Allay.class, mob -> {
                mob.setCanPickupItems(false);
                mob.setCollidable(true);
                mob.getEquipment().setItemInMainHand(new ItemStack(Material.GLASS));

                // Mob health
                mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(attributes.mobHealth());
                mob.setHealth(attributes.mobHealth());
            });
            allays.add(allay);
            Bukkit.getMobGoals().addGoal(allay, 0, new AllayRepairGoal(allay, hitBlock, attributes));
        }
        return allays;
    }

    private static class FlareListener implements LaunchableListener {

        @EventHandler
        public void playerFireFlare(PlayerInteractEvent e){
            handleLaunch(e, Flare.class, FlareAttributes.class);
        }

        @EventHandler
        public void flareCollide(SentinelProjectileCollideEvent e){
            if(!e.getEntity().isValid()) return;
            Entity entity = e.getEntity();
            Optional<FlareAttributes> optionalAttributes;
            if((optionalAttributes = SentinelDataWrapper.getInstance().loadPDC(entity, FlareAttributes.class)).isEmpty()) return;
            FlareAttributes attributes = optionalAttributes.get();
            Block block = e.getHitBlock();
            Flare.createAllays(e.getHitBlockFace().getDirection(), block.getLocation(), attributes);
            e.getEntity().remove();
        }
    }
}
