package dev.tom.sentinels.projectiles;

import dev.tom.sentinels.data.PDCTransferResult;
import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.physics.BasicPhysics;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.inventory.ItemStack;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

public abstract class AttributeLaunchable<T extends Serializable> extends Launchable<T> {

    private final Class<T> type;


    public AttributeLaunchable(ItemStack item, BlockData blockData, Class<T> type) {
        super(item, blockData);
        this.type = type;
    }

    @Override
    protected Optional<PDCTransferResult<T, BlockDisplay>> handleAttributes(BlockDisplay display) {
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
}
