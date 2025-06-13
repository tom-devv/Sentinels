package dev.tom.sentinels.projectiles;

import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.data.PDCTransferResult;

import dev.tom.sentinels.data.SentinelDataWrapper;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.Serializable;
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


    public <I extends ItemStack, T extends Serializable, E extends Entity> Optional<PDCTransferResult<T, E>> launchEntity(I item, Player player, Class<T> type) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();


        FallingBlock falling = location.getWorld().spawn(location, FallingBlock.class, spawned -> {
            spawned.setVelocity(direction);
            spawned.setBlockData(item.getType().createBlockData());
        });

        // Collision Detector
        new CollisionDetector(Sentinels.getInstance(), falling).detect();

        Optional<PDCTransferResult<T, Entity>> optionalResult = SentinelDataWrapper.getInstance().transferItemPDC(item, falling, type);
        if (optionalResult.isPresent()) {
            T attributes = optionalResult.get().data();

            //Check if gravity then assign to entity
            if (Gravity.class.isAssignableFrom(type)) {
                if (attributes instanceof Gravity gravityAttributes) {
                    falling.setGravity(gravityAttributes.gravity());
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
