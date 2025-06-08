package dev.tom.sentinels.regions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.UUID;
import java.util.Set;

public interface Region {

    /**
     * Gets the unique identifier for this region.
     * @return A UUID representing the region's ID.
     */
    UUID getId();

    /**
     * Gets the user-friendly name of this region.
     * @return The region's display name.
     */
    String getName();

    /**
     * Sets the user-friendly name of this region.
     * @param name The new display name.
     */
    void setName(String name);

    /**
     * Gets the world this region belongs to.
     * @return The World object.
     */
    World getWorld();

    /**
     * Checks if a given location (specifically its block coordinates) is within this region.
     * This is the core spatial check for block-level containment.
     * @param location The location to check. Its block coordinates (X, Y, Z) will be used.
     * @return true if the block at the location's coordinates is inside the region, false otherwise.
     */
    boolean contains(Location location);

    /**
     * Checks if a given Block object is entirely within this region.
     * This method is a convenience wrapper that uses the block's location for the check.
     * @param block The Block object to check.
     * @return true if the block is inside the region, false otherwise.
     */
    boolean contains(Block block);

    /**
     * Gets the minimum bounding box coordinates of this region.
     * Useful for quick broad-phase checks, even for non-rectangular regions.
     * @return A Location representing the minimum X, Y, Z coordinates.
     */
    Location getMinBounds();

    /**
     * Gets the maximum bounding box coordinates of this region.
     * Useful for quick broad-phase checks.
     * @return A Location representing the maximum X, Y, Z coordinates.
     */
    Location getMaxBounds();

    /**
     * Gets a set of Location objects representing the block coordinates
     * that lie on the outer boundary (perimeter/surface) of this region.
     * This is useful for drawing outlines, checking for boundary interactions,
     * or applying effects specifically to the surface of the region.
     *
     * @return A Set of Location objects, each representing a block on the region's perimeter.
     */
    Set<Location> getPerimeter();

    /**
     * Determines the outward-facing sides (BlockFaces) of a given block,
     * assuming the block is on the boundary of this region.
     * An "outward face" is a side of the block that, if moved one step in that direction,
     * would lead outside the region.
     *
     * This method is useful for visual effects (e.g., highlighting the outer edge),
     * or for determining interaction points on the region's boundary.
     *
     * @param blockLocation The Location of the block to check.
     * @return A Set of BlockFace enums representing the outward faces.
     * Returns an empty set if the block is not part of the region's boundary
     * or if it's an interior block.
     */
    Set<BlockFace> getOutwardFaces(Location blockLocation);

    /**
     * Gets a mutable set of UUIDs of players who are members of this region.
     * Members might have special permissions or properties within the region.
     * @return A Set of player UUIDs.
     */
    Set<UUID> getMembers();

    /**
     * Gets a mutable set of UUIDs of players who are owners of this region.
     * Owners typically have full administrative control over the region.
     * @return A Set of player UUIDs.
     */
    Set<UUID> getOwners();

    /**
     * Gets a specific flag value associated with this region.
     * Flags define rules or properties of the region (e.g., PVP, mob spawning).
     * @param flagType The class representing the type of flag.
     * @param <T> The type of the flag value.
     * @return The value of the flag, or null if not set.
     */
    <T> T getFlag(RegionFlag<T> flagType);

    /**
     * Sets a specific flag value for this region.
     * @param flagType The class representing the type of flag.
     * @param value The value to set for the flag.
     * @param <T> The type of the flag value.
     */
    <T> void setFlag(RegionFlag<T> flagType, T value);

    /**
     * Removes a specific flag from this region.
     * @param flagType The class representing the type of flag.
     */
    void removeFlag(RegionFlag<?> flagType);

    /**
     * Gets a map of all flags currently set for this region.
     * @return A Map where keys are RegionFlag instances and values are their corresponding flag values.
     */
    java.util.Map<RegionFlag<?>, Object> getFlags();

}