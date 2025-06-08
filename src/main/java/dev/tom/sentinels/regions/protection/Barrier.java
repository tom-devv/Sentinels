package dev.tom.sentinels.regions.protection;

import dev.tom.sentinels.regions.Region;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.UUID;

/**
 * Represents a protectable and repairable "barrier" block associated with a region.
 * This interface defines the contract for any block that serves as a protective element
 * on a region's perimeter, capable of having health and being repaired.
 */
public interface Barrier extends Healable {

    UUID getId();

    Location getLocation();

    Block getBlock();

    /**
     * Destroy the barrier block
     */
    void destroy();

    /**
     * Gets the region the barrier is a part of
     * @return the parent region
     */
    Region getRegion();

}
