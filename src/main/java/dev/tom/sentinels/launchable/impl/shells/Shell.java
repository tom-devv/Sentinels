package dev.tom.sentinels.launchable.impl.shells;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.launchable.AbstractLaunchable;
import dev.tom.sentinels.launchable.LaunchableListener;
import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.BarrierManager;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Shell extends AbstractLaunchable<ShellAttributes>  {

    public Shell(ItemStack item, BlockData blockData, Class<ShellAttributes> type) {
        super(item, blockData, type);
    }

    private static class ShellListeners implements LaunchableListener {
        @EventHandler
        public void projectileExplosion(EntityExplodeEvent e){
            if(!(e.getEntity() instanceof TNTPrimed tnt)) return;
            Optional<ShellAttributes> optionalData = SentinelDataWrapper.getInstance().loadPDC(tnt, ShellAttributes.class);
            if(optionalData.isEmpty()) return;
            ShellAttributes attributes = optionalData.get();
            List<Block> blockList = new ArrayList<>(e.blockList());
            e.blockList().clear(); // Don't actually damage blocks;
            blockList.forEach(block -> {
                Barrier barrier = BarrierManager.getInstance().getBarrier(block.getLocation());
                if(barrier != null) {
                    barrier.damage(attributes.damage());
                }
            });
        }

        @EventHandler
        public void projectileHit(ProjectileHitEvent e){
            Projectile projectile = e.getEntity();
            Optional<ShellAttributes> optionalData = SentinelDataWrapper.getInstance().loadPDC(projectile, ShellAttributes.class);
            if(optionalData.isEmpty()) return;
            ShellAttributes attributes = optionalData.get();
            Block block = e.getHitBlock();
            if(block == null) return; // hit entity instead
            Barrier barrier = BarrierManager.getInstance().getBarrier(block.getLocation());
            if(barrier == null) return;
            // Spawn explosion to handle blocklist radius etc
            World world = block.getWorld();
            world.spawn(block.getLocation(), TNTPrimed.class, tnt -> {
                SentinelDataWrapper.getInstance().savePDC(tnt, attributes);
                tnt.setYield((float) attributes.radius());
                tnt.setFuseTicks(0); // Explode quickly
                tnt.setGravity(false); // Don't move down at all
            });
        }

        /**
         * Copy data from Arrow ItemStack PDC to projectile PDC
         */
        @EventHandler
        public void projectileFire(EntityShootBowEvent e){
            ItemStack item = e.getConsumable();
            if(item == null) return;
            SentinelDataWrapper.getInstance().transferItemPDC(item, e.getProjectile(), ShellAttributes.class)
                    .ifPresent(result -> {
                        result.entity().setGravity(result.data().gravity());
                    });
        }
    }
}
