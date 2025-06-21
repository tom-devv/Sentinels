package dev.tom.sentinels.launchable.impl.shells;

import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.events.SentinelProjectileLaunchEvent;
import dev.tom.sentinels.launchable.AbstractLaunchable;
import dev.tom.sentinels.launchable.LaunchableListener;
import dev.tom.sentinels.launchable.impl.flares.Flare;
import dev.tom.sentinels.launchable.impl.flares.FlareAttributes;
import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.BarrierManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Shell extends AbstractLaunchable<ShellAttributes>  {

    private final ShellAttributes attributes;

    public Shell(ItemStack item) {
        super(item, Material.TNT.createBlockData(), ShellAttributes.class);
        Optional<ShellAttributes> opt = SentinelDataWrapper.getInstance().loadPDC(item, ShellAttributes.class);
        if(opt.isEmpty()){
            throw new RuntimeException("Could not load attributes from item: " + item + " " + type);
        } else {
            attributes = opt.get();
        }
    }

    private static class ShellListeners implements LaunchableListener {
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
            Optional<ShellAttributes> optionalData;
            if ((optionalData = SentinelDataWrapper.getInstance().loadPDC(entity, ShellAttributes.class)).isEmpty()) return;
            ShellAttributes attributes = optionalData.get();
            Block block = e.getHitBlock();
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
            entity.remove();
        }

        @EventHandler
        public void playerFireShell(PlayerInteractEvent e){
            handleLaunch(e, Shell.class, ShellAttributes.class);
        }

    }
}
