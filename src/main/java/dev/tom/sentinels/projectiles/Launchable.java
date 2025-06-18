package dev.tom.sentinels.projectiles;

import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.data.PDCTransferResult;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.events.SentinelProjectileLaunchEvent;
import dev.tom.sentinels.physics.BasicPhysics;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.Optional;

public abstract class Launchable<T extends Serializable> {

    protected final ItemStack item;
    protected final BlockData blockData;

    public Launchable(ItemStack item, BlockData blockData) {
        this.item = item;
        this.blockData = blockData;
    }

    public final Optional<PDCTransferResult<T, BlockDisplay>> launch(Location location) {
        Vector direction = location.getDirection();
        BlockDisplay display = location.getWorld().spawn(location, BlockDisplay.class, entity -> {
            entity.setBlock(blockData);
            entity.setRotation(location.getYaw(), location.getPitch());
            entity.setVelocity(direction.normalize());
            entity.setTeleportDuration(2);
            entity.setInterpolationDuration(5);
            entity.setTransformation(new Transformation(
                    new Vector3f(-0.5f, -0.5f, -1f),
                    new Quaternionf(),
                    new Vector3f(1, 1, 1),
                    new Quaternionf()
            ));
        });

        SentinelProjectileLaunchEvent event = new SentinelProjectileLaunchEvent(display);
        Sentinels.getInstance().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            display.remove();
            return Optional.empty();
        }

        new BasicPhysics(display); // add some basic physics to it

        return handleAttributes(display);
    }

    protected Optional<PDCTransferResult<T, BlockDisplay>> handleAttributes(BlockDisplay display) {
        return Optional.empty();
    }

}
