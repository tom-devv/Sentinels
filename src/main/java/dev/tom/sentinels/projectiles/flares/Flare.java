package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.events.SentinelProjectileLaunchEvent;
import dev.tom.sentinels.projectiles.AttributeLaunchable;
import dev.tom.sentinels.projectiles.LaunchableListener;
import dev.tom.sentinels.utils.MobCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;


public class Flare extends AttributeLaunchable<FlareAttributes> implements LaunchableListener {

    public Flare(ItemStack item) {
        super(item, Material.REDSTONE_BLOCK.createBlockData(), FlareAttributes.class);
    }

    @Override
    public void registerListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new FlareListener(), plugin);
    }


    private static class FlareListener implements Listener {
        @EventHandler
        public void playerFireFlare(PlayerInteractEvent e){
            // Only fire when interacting with air
            if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
            if(e.getItem() == null) return;
            Player player = e.getPlayer();
            ItemStack item = e.getItem();
            if(!SentinelDataWrapper.getInstance().isType(item.getItemMeta(), FlareAttributes.class)) return;
            // Not a flare, can't fire
            Flare flare = new Flare(item);
            flare.launch(player.getEyeLocation());
        }

        @EventHandler
        public void flareLaunch(SentinelProjectileLaunchEvent e){
            if(!SentinelDataWrapper.getInstance().isType(e.getEntity(), FlareAttributes.class)) return;
            Optional<FlareAttributes> optionalAttributes = SentinelDataWrapper.getInstance().loadPDC(e.getEntity(), FlareAttributes.class);
        }

        @EventHandler
        public void entityCollide(SentinelProjectileCollideEvent e){
            if(!e.getEntity().isValid()) return;
            Entity entity = e.getEntity();
            if(!SentinelDataWrapper.getInstance().isType(entity, FlareAttributes.class)) return;
            // Flares only
            Optional<FlareAttributes> optionalAttributes = SentinelDataWrapper.getInstance().loadPDC(entity, FlareAttributes.class);
            if(optionalAttributes.isEmpty()) return;
            FlareAttributes attributes = optionalAttributes.get();
            Block block = e.getHitBlock();
            MobCreator.createAllays(block.getLocation().add(e.getHitBlockFace().getDirection()), block.getLocation(), attributes);
            e.getEntity().remove(); // Remove entity now PDC transferred
        }
    }
}
