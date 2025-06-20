package dev.tom.sentinels.launchable.impl.bomb;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.launchable.AbstractLaunchable;
import dev.tom.sentinels.launchable.LaunchableListener;
import dev.tom.sentinels.launchable.impl.flares.Flare;
import dev.tom.sentinels.launchable.impl.flares.FlareAttributes;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Bomb extends AbstractLaunchable<BombAttributes> {

    public Bomb(ItemStack item) {
        super(item, Material.SHULKER_SHELL.createBlockData(), BombAttributes.class);
    }

    /**
     * Override to increase scale as bomb is big
     */
    @Override
    protected @NotNull BlockDisplay createDisplay(Location location) {
        Vector direction = location.getDirection();
        BlockDisplay display = location.getWorld().spawn(location, BlockDisplay.class, entity -> {
            entity.setRotation(location.getYaw(), location.getPitch());
            entity.setVelocity(direction.normalize());
            entity.setTransformation(new Transformation(
                    new Vector3f(-0.5f, -0.5f, -1f),
                    new Quaternionf(),
                    new Vector3f(3, 3, 3),
                    new Quaternionf()
            ));
        });
        setDisplayBasics(display);
        return display;
    }


    private class BombListeners implements LaunchableListener {


        @EventHandler
        public void playerFireFlare(PlayerInteractEvent e){
            // Only fire when interacting with air
            if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
            if(e.getItem() == null) return;
            Player player = e.getPlayer();
            ItemStack item = e.getItem();
            if(!SentinelDataWrapper.getInstance().isType(item.getItemMeta(), BombAttributes.class)) {
                return;
            }
            // Not a flare, can't fire
            Bomb bomb = new Bomb(item);
            bomb.launch(player.getEyeLocation(), player);
        }
    }
}
