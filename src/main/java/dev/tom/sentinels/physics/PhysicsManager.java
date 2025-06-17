package dev.tom.sentinels.physics;

import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.data.PDCTransferResult;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.projectiles.Gravity;
import dev.tom.sentinels.projectiles.Velocity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.Optional;

/**
 * Manages active physics objects like flares and shells
 */
public class PhysicsManager {

    private static PhysicsManager INSTANCE;

    private PhysicsManager() {
    }

    public static PhysicsManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PhysicsManager();
        }
        return INSTANCE;
    }


    public <I extends ItemStack, T extends Serializable, E extends Entity> Optional<PDCTransferResult<T, E>> launchEntity(I item, Player player, Class<T> type) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();

        // Offset to launch at right hand
        // This means it does NOT align with crosshair
        //-0.7 0.3
        Location launchLocation = new Location(location.getWorld(), location.getX() - 0.7, location.getY() - 0.3, location.getZ());
        BlockDisplay display = location.getWorld().spawn(launchLocation, BlockDisplay.class, spawned -> {
            spawned.setBlock(Material.GLOWSTONE.createBlockData());
            spawned.setVelocity(direction.normalize());
            spawned.setTeleportDuration(2);
            spawned.setInterpolationDuration(5);
            spawned.setTransformation(new Transformation(
                    new Vector3f(-0.5f,0f,0f),
                    new Quaternionf(),
                    new Vector3f(1,1,1),
                    new Quaternionf()
            ));
        });

        Optional<PDCTransferResult<T, Entity>> optionalResult = SentinelDataWrapper.getInstance().transferItemPDC(item, display, type);
        if (optionalResult.isPresent()) {
            T attributes = optionalResult.get().data();

            //Check if gravity then assign to entity
            if (attributes instanceof Gravity gravityAttributes) {
                display.setGravity(gravityAttributes.gravity());
            }
            if(attributes instanceof Velocity velocityAttributes) {
                display.setVelocity(display.getVelocity().multiply(velocityAttributes.velocity()));
            }

            // Physics
            new DisplayPhysics(display);

            @SuppressWarnings("unchecked")
            Optional<PDCTransferResult<T, E>> finalResult = Optional.of(
                    new PDCTransferResult<>(attributes, (E) display)
            );
            return finalResult;
        } else {
            // Should never fire
            System.err.println("Failed to transfer PDC for ItemStack: " + item);
            return Optional.empty();
        }
        // Physics
    }

    private void projectileCheckupTask(Entity entity, Vector velocity){
        projectileCheckupTask(entity, velocity, 20 * 15); // 15 second timeout
    }

    /**
     * Run tasks every tick for an entity
     * @param entity
     * @param velocity
     */
    private void projectileCheckupTask(Entity entity, Vector velocity, int timeout) {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if(!entity.isValid() || i > timeout) {
                    cancel();
                }
                // Do not touch y velocity
                entity.setVelocity(new Vector(velocity.getX(), entity.getVelocity().getY(), velocity.getZ()));
                i++;
            }
        }.runTaskTimer(Sentinels.getInstance(), 0L, 1);
    }

}
