package dev.tom.sentinels.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.World;
import java.util.HashSet;
import java.util.Set;

public class BlockUtil {

    /**
     * Gets all blocks within a specified spherical radius of a given center location.
     * The radius is measured from the center of the given Location to the center of the blocks.
     *
     * @param center The center location around which to search for blocks.
     * This can be a fractional location, and the distance calculation
     * will be from this exact point.
     * @param radius The radius in blocks. Only blocks whose centers are
     * within this radius will be included.
     * @return A Set of Block objects within the specified radius. Returns an empty set
     * if the provided center location's world is null.
     */
    public static Set<Block> getBlocksInRadius(Location center, double radius) {
        Set<Block> blocks = new HashSet<>();
        World world = center.getWorld();

        // Essential: Ensure the world exists for the given location
        if (world == null) {
            return blocks; // Return an empty set if the world is null
        }

        // Calculate the square of the radius for faster distance checks.
        // Using distanceSquared avoids computationally expensive square root operations.
        double radiusSquared = radius * radius;

        // Determine the bounding box for the iteration.
        // We calculate the min and max coordinates (inclusive) of the cube
        // that completely encompasses the sphere of the given radius.
        // Using Math.floor and Math.ceil ensures we cover all relevant block coordinates,
        // even if the center is fractional or the radius extends slightly beyond integer boundaries.
        int minX = (int) Math.floor(center.getX() - radius);
        int minY = (int) Math.floor(center.getY() - radius);
        int minZ = (int) Math.floor(center.getZ() - radius);

        int maxX = (int) Math.ceil(center.getX() + radius);
        int maxY = (int) Math.ceil(center.getY() + radius);
        int maxZ = (int) Math.ceil(center.getZ() + radius);

        // Iterate through all blocks within the calculated bounding box
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);

                    // Calculate the distance from the center of the *block* to the given *center* location.
                    // Adding 0.5 to block's X, Y, Z gets its center coordinates.
                    // Using distanceSquared() for performance comparison with radiusSquared.
                    double distanceSquared = block.getLocation().add(0.5, 0.5, 0.5).distanceSquared(center);

                    // If the block's center is within or exactly on the radius, add it to the set.
                    if (distanceSquared <= radiusSquared) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }
}