package dev.tom.sentinels.launchables.impl.shells;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.launchables.Launchable;
import dev.tom.sentinels.items.ItemListener;
import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.BarrierManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Shell extends Launchable<ShellAttributes> {


    public Shell(ItemStack item) {
        super(item, Material.TNT.createBlockData(), ShellAttributes.class);
    }

    private void collision(SentinelProjectileCollideEvent e) {
        Block block = e.getHitBlock();
        // Spawn explosion to handle blocklist radius etc
        World world = block.getWorld();
        world.spawn(block.getLocation(), TNTPrimed.class, tnt -> {
            SentinelDataWrapper.getInstance().savePDC(tnt, attributes);
            tnt.setYield((float) attributes.radius());
            tnt.setFuseTicks(0); // Explode quickly
            tnt.setGravity(false); // Don't move down at all
        });
    }


    private static class ShellListeners implements ItemListener<Shell> {
        /**
         * This is TNT exploding not a launchable
         * TNT is used here to get a blocklist rather
         * than search for blocks in a radius
         * @param e
         */
        @EventHandler
        public void projectileExplosion(EntityExplodeEvent e){
            if(!(e.getEntity() instanceof TNTPrimed tnt)) return;
            Optional<ShellAttributes> optionalData;
            if((optionalData = SentinelDataWrapper.getInstance().loadPDC(tnt, ShellAttributes.class)).isEmpty()) return;
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
        public void projectileHit(SentinelProjectileCollideEvent e){
            if(!e.getEntity().isValid()) return;
            Entity entity = e.getEntity();
            getLaunchable(entity).ifPresent(shell -> {
                shell.collision(e);
                shell.remove();
            });
            entity.remove();
        }

        @EventHandler
        public void playerFireShell(PlayerInteractEvent e){
            handleLaunch(e, Shell.class, ShellAttributes.class);
        }

    }
}
