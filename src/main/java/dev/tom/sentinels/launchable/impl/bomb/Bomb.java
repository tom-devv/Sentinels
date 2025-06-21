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

import java.util.function.Consumer;

public class Bomb extends AbstractLaunchable<BombAttributes> {

    public Bomb(ItemStack item) {
        super(item, Material.SHULKER_SHELL.createBlockData(), BombAttributes.class);
    }


    @Override
    protected Consumer<? super BlockDisplay> displaySettings(Location location) {
        Vector direction = location.getDirection();
        return display -> {
            display.setRotation(location.getYaw(), location.getPitch());
            display.setVelocity(direction.normalize());
            display.setTransformation(new Transformation(
                    new Vector3f(-0.5f, -0.5f, -1f),
                    new Quaternionf(),
                    new Vector3f(3, 3, 3),
                    new Quaternionf()
            ));
            display.setBlock(blockData);
            display.setTeleportDuration(2);
            display.setInterpolationDuration(5);
            display.setInvulnerable(true);
        };
    }

    private static class BombListeners implements LaunchableListener {


        @EventHandler
        public void playerFireFlare(PlayerInteractEvent e){
            handleLaunch(e, Bomb.class, BombAttributes.class);
        }
    }
}
