package dev.tom.sentinels.projectiles;

import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.data.Gravity;
import dev.tom.sentinels.data.SentinelDataWrapper;

import dev.tom.sentinels.events.EntityCollisionEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProjectileManager {

    private static ProjectileManager INSTANCE;

    private ProjectileManager() {
    }

    public static ProjectileManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ProjectileManager();
        }
        return INSTANCE;
    }

    private Map<Entity, BukkitTask> trackedEntities = new HashMap<>();

    /**
     * @param item   item with PDC data
     * @param entity entity to transfer PDC to
     * @param <I>
     * @param <E>
     * @param <T>
     * @return a pair of pdc data and optional entity spawned
     */
    public <I extends ItemStack, E extends Entity, T extends Serializable> Optional<PDCTransferResult<T, E>> transferPDC(I item, E entity, Class<T> type) {
        Optional<T> optionalData = SentinelDataWrapper.getInstance().loadPDC(item.getItemMeta(), type);
        if (optionalData.isEmpty()) return Optional.empty();
        T attributes = optionalData.get();
        SentinelDataWrapper.getInstance().savePDC(entity, attributes);
        return Optional.of(new PDCTransferResult<>(attributes, entity));
    }

    public <I extends ItemStack, T extends Serializable, E extends Entity> Optional<PDCTransferResult<T, E>> launchEntity(I item, Player player, Class<T> type) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();


        FallingBlock falling = location.getWorld().spawn(location, FallingBlock.class, spawned -> {
            spawned.setVelocity(direction);
            spawned.setBlockData(item.getType().createBlockData());
        });

        // Collision Detector
        new CollisionDetector(Sentinels.getInstance(), falling).detect();

        Optional<PDCTransferResult<T, Entity>> optionalResult = transferPDC(item, falling, type);
        if (optionalResult.isPresent()) {
            T attributes = optionalResult.get().data();

            //Check if gravity then assign to entity
            if (Gravity.class.isAssignableFrom(type)) {
                if (attributes instanceof Gravity gravityAttributes) {
                    falling.setGravity(gravityAttributes.hasGravity());
                }
            }

            @SuppressWarnings("unchecked")
            Optional<PDCTransferResult<T, E>> finalResult = Optional.of(
                    new PDCTransferResult<>(attributes, (E) falling)
            );
            return finalResult;
        } else {
            // Should never fire
            System.err.println("Failed to transfer PDC for ItemStack: " + item);
            return Optional.empty();
        }
    }

}
