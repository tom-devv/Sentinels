package dev.tom.sentinels.projectiles;

import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.data.PDCTransferResult;
import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.events.SentinelProjectileLaunchEvent;
import dev.tom.sentinels.physics.BasicPhysics;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;

public abstract class Launchable<T extends Serializable> {

    protected final ItemStack item;
    protected final BlockData blockData;
    protected final Class<T> type;

    public Launchable(ItemStack item, @NotNull BlockData blockData, @NotNull Class<T> type) {
        this.blockData = blockData;
        this.type = type;
        this.item = item;
        if (this instanceof LaunchableListener listener) {
            listener.registerListener(Sentinels.getInstance());
        }
    }

    protected @Nullable BlockDisplay display;

    public final Optional<PDCTransferResult<T, BlockDisplay>> launch(Location location) {
        this.display = createDisplay(location);

        if(!callEvent()) {
            return Optional.empty();
        }
        initPhysics();
        return handleAttributes();
    }

    protected Optional<PDCTransferResult<T, BlockDisplay>> handleAttributes() {
        Optional<PDCTransferResult<T, BlockDisplay>> optionalResult = SentinelDataWrapper.getInstance().transferItemPDC(this.item, display, type);
        if (optionalResult.isPresent()) {
            T attributes = optionalResult.get().data();

            if (attributes instanceof Gravity gravityAttributes) {
                display.setGravity(gravityAttributes.gravity());
            }
            if(attributes instanceof Velocity velocityAttributes) {
                display.setVelocity(display.getVelocity().multiply(velocityAttributes.velocity()));
            }

            @SuppressWarnings("unchecked")
            Optional<PDCTransferResult<T, BlockDisplay>> finalResult = Optional.of(
                    new PDCTransferResult<>(attributes, display)
            );
            return finalResult;
        } else {
            // Should never fire
            System.err.println("Failed to transfer PDC for ItemStack: " + item);
            return Optional.empty();
        }
    }

    private BlockDisplay createDisplay(Location location) {
        Vector direction = location.getDirection();
        return location.getWorld().spawn(location, BlockDisplay.class, entity -> {
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
    }

    /**
     * @return cancelled
     */
    private boolean callEvent() {
        SentinelProjectileLaunchEvent event = new SentinelProjectileLaunchEvent(this.display);
        Sentinels.getInstance().getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            this.display.remove();
            return true;
        }
        return false;
    }

    private void initPhysics() {
        new BasicPhysics(this.display);
    }

}
