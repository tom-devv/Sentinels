package dev.tom.sentinels.regions.impl;

import dev.tom.sentinels.regions.Region;
import dev.tom.sentinels.regions.RegionFlag;
import dev.tom.sentinels.regions.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CuboidRegion implements Region {

    private final UUID id;
    private String name;
    protected final World world;
    protected final int minX, minY, minZ;
    protected final int maxX, maxY, maxZ;
    private final Set<Location> perimeterCache = new HashSet<>();

    private final Set<UUID> members = new HashSet<>();
    private final Set<UUID> owners = new HashSet<>();
    private final Map<RegionFlag<?>, Object> flags = new HashMap<>();

    protected CuboidRegion(String name, Location point1, Location point2) {
        if(point1.getWorld() != point2.getWorld()) throw new IllegalArgumentException("Both points must be the same world");
        this.id = UUID.randomUUID();
        this.name = name;
        this.world = point1.getWorld();

        this.minX = Math.min(point1.getBlockX(), point2.getBlockX());
        this.minY = Math.min(point1.getBlockY(), point2.getBlockY());
        this.minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        this.maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        this.maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());

        RegionManager.getInstance().add(this);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }

        int locX = location.getBlockX();
        int locY = location.getBlockY();
        int locZ = location.getBlockZ();

        return locX >= minX && locX <= maxX &&
                locY >= minY && locY <= maxY &&
                locZ >= minZ && locZ <= maxZ;
    }

    /**
     * Checks if a given Block object is entirely within this region.
     * This method is a convenience wrapper that delegates to the contains(Location) method
     * using the block's location.
     *
     * @param block The Block object to check.
     * @return true if the block is inside the region, false otherwise.
     */
    @Override
    public boolean contains(Block block) {
        // Delegate to the contains(Location) method, as it already performs the block-level check
        return contains(block.getLocation());
    }

    /**
     * Gets a set of Location objects representing the block coordinates
     * that lie on the 3D edges (the skeleton) of this cuboid region.
     * This includes the perimeter of the bottom face, the perimeter of the top face,
     * and the four vertical pillars connecting them.
     *
     * @return A Set of Location objects, each representing a block on one of the region's 3D edges.
     */
    @Override
    public Set<Location> getPerimeter() {
        if(!perimeterCache.isEmpty()) return perimeterCache;
        Set<Location> edgeBlocks = new HashSet<>();

        // Add blocks for the 12 edges of the cuboid

        // 1. Edges parallel to X-axis (4 lines)
        // Bottom front, top front, bottom back, top back
        for (int x = minX; x <= maxX; x++) {
            edgeBlocks.add(new Location(world, x, minY, minZ)); // Bottom front
            edgeBlocks.add(new Location(world, x, minY, maxZ)); // Bottom back
            edgeBlocks.add(new Location(world, x, maxY, minZ)); // Top front
            edgeBlocks.add(new Location(world, x, maxY, maxZ)); // Top back
        }

        // 2. Edges parallel to Z-axis (4 lines)
        // Bottom left, top left, bottom right, top right
        // Iterate from minZ + 1 to maxZ - 1 to avoid double-adding corners already covered by X-loops
        for (int z = minZ + 1; z < maxZ; z++) {
            edgeBlocks.add(new Location(world, minX, minY, z)); // Bottom left
            edgeBlocks.add(new Location(world, maxX, minY, z)); // Bottom right
            edgeBlocks.add(new Location(world, minX, maxY, z)); // Top left
            edgeBlocks.add(new Location(world, maxX, maxY, z)); // Top right
        }

        // 3. Edges parallel to Y-axis (4 pillars)
        // Iterate from minY + 1 to maxY - 1 to avoid double-adding corners already covered by X/Z-loops
        for (int y = minY + 1; y < maxY; y++) {
            edgeBlocks.add(new Location(world, minX, y, minZ)); // Front-left pillar
            edgeBlocks.add(new Location(world, maxX, y, minZ)); // Front-right pillar
            edgeBlocks.add(new Location(world, minX, y, maxZ)); // Back-left pillar
            edgeBlocks.add(new Location(world, maxX, y, maxZ)); // Back-right pillar
        }
        perimeterCache.addAll(edgeBlocks);
        return edgeBlocks;
    }

    /**
     * Gets the blocks immediately outside the regions outward faces
     * @param blockLocation The Location of the block to check.
     * @return
     */
    @Override
    public Set<BlockFace> getOutwardFaces(Location blockLocation) {
        Set<BlockFace> outwardFaces = new HashSet<>();

        // Ensure the block is in the same world as the region
        if (!blockLocation.getWorld().equals(world)) {
            return outwardFaces;
        }

        if(!contains(blockLocation)) {
            return outwardFaces;
        }

        // Iterate through all cardinal block faces
        for (BlockFace face : BlockFace.values()) {
            // We only care about cardinal directions (North, South, East, West)
            if (face == BlockFace.SELF || !face.isCartesian() || face == BlockFace.UP || face == BlockFace.DOWN) {
                continue;
            }

            Block adjacent = blockLocation.getBlock().getRelative(face);

            // If adjacent isn't in the region then this face points outwards
            if (!contains(adjacent)) {
                outwardFaces.add(face);
            }
        }
        return outwardFaces;
    }

    /**
     * Gets a set of Location objects representing all block coordinates
     * that lie on the four vertical faces (walls) of this cuboid region.
     * This includes all blocks from minY to maxY on the minX, maxX, minZ, and maxZ boundaries.
     * It does NOT include blocks on the top or bottom horizontal faces,
     * unless they also happen to be part of a vertical face.
     *
     * @return A Set of Location objects, each representing a block on one of the region's vertical faces.
     */
    public Set<Location> getVerticalFaces() {
        Set<Location> verticalFaceBlocks = new HashSet<>();

        // Use the stored integer bounds from the class members
        // minX, minY, minZ, maxX, maxY, maxZ, and world are already available as class members.

        // Iterate X and Y for the front (minZ) and back (maxZ) walls
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                verticalFaceBlocks.add(new Location(world, x, y, minZ)); // Front wall
                verticalFaceBlocks.add(new Location(world, x, y, maxZ)); // Back wall
            }
        }

        // Iterate Z and Y for the left (minX) and right (maxX) walls
        // Start Z from minZ + 1 and go up to maxZ - 1 to avoid double-adding
        // the blocks that are part of the front and back walls (corners and edges).
        for (int z = minZ + 1; z < maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                verticalFaceBlocks.add(new Location(world, minX, y, z)); // Left wall
                verticalFaceBlocks.add(new Location(world, maxX, y, z)); // Right wall
            }
        }

        return verticalFaceBlocks;
    }

    @Override
    public Location getMinBounds() {
        return new Location(world, minX, minY, minZ);
    }

    @Override
    public Location getMaxBounds() {
        return new Location(world, maxX, maxY, maxZ);
    }

    @Override
    public Set<UUID> getMembers() {
        return members;
    }

    @Override
    public Set<UUID> getOwners() {
        return owners;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getFlag(RegionFlag<T> flagType) {
        return (T) flags.getOrDefault(flagType, flagType.getDefaultValue());
    }

    @Override
    public <T> void setFlag(RegionFlag<T> flagType, T value) {
        flags.put(flagType, value);
    }

    @Override
    public void removeFlag(RegionFlag<?> flagType) {
        flags.remove(flagType);
    }

    @Override
    public Map<RegionFlag<?>, Object> getFlags() {
        return new HashMap<>(flags); // Return a copy to prevent external modification
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CuboidRegion that = (CuboidRegion) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}