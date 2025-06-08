package dev.tom.sentinels.regions.protection;

import dev.tom.sentinels.regions.Region;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StandardBarrier implements Barrier {

    private final double MAX_HEALTH = 1;
    private final Material FULL_HEALTH_MATERIAL = Material.WHITE_STAINED_GLASS;

    private final UUID id;
    private final Region region;
    private final Location location;
    private final Block block; // todo
    private double health = MAX_HEALTH;

    public StandardBarrier(Region region, Location location) {
        id = UUID.randomUUID();
        this.region = region;
        this.location = location;
        this.block = location.getBlock();

        this.getBlock().setType(FULL_HEALTH_MATERIAL);
        BarrierManager.getInstance().add(this);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    @Override
    public double getHealth() {
        return this.health;
    }

    @Override
    public double getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    public double damage(double amount) {
        double newHealth = Math.min(health - amount, MAX_HEALTH);
        setHealth(newHealth);
        return health;
    }

    @Override
    public double repair(double amount) {
        double newHealth = Math.max(health + amount, MAX_HEALTH);
        setHealth(newHealth);
        return newHealth;
    }

    @Override
    public void setHealth(double health) {
        this.health = health;
        if(isDead() || health < 0) {
            destroy();
            return;
        }
        updateVisualState();
    }

    @Override
    public boolean isDead() {
        return getBlock().getType().isAir() || getHealth() < 0; // TODO maybe change this
    }

    @Override
    public void destroy() { //TODO IMPL
        getBlock().setType(Material.AIR);
    }

    @Override
    public Region getRegion() {
        return this.region;
    }

    @Override
    public void fullyRepair() {
        setHealth(MAX_HEALTH);
    }

    public void updateVisualState() {
        if (block == null || !block.getChunk().isLoaded()) return; // Block not loaded

        Material targetMaterial;

        // Calculate health percentage
        double healthPercentage = health / MAX_HEALTH;

        // Determine color based on health percentage
        if(healthPercentage == 1) {
            targetMaterial = FULL_HEALTH_MATERIAL;
        } else if (healthPercentage > 0.8) {
            targetMaterial = Material.LIME_STAINED_GLASS; // Green (80-100%)
        } else if (healthPercentage > 0.6) {
            targetMaterial = Material.YELLOW_STAINED_GLASS; // Yellow (60-80%)
        } else if (healthPercentage > 0.4) {
            targetMaterial = Material.ORANGE_STAINED_GLASS; // Orange (40-60%)
        } else if (healthPercentage > 0.2) {
            targetMaterial = Material.BROWN_STAINED_GLASS; // Brown/Murky (20-40%)
        } else  {
            targetMaterial = Material.RED_STAINED_GLASS; // Red (0-20%), less than 0 would be destroy() has been called
        }

        // Apply the new material
        if (block.getType() != targetMaterial) {
            block.setType(targetMaterial);
        }
        // --- NEW: Send block damage packet for crack lines ---
        // Calculate NMS damage progress (0-9, where 9 is almost broken)
        // If health is 100%, progress is 0. If health is 0%, progress is 9.
        int damageProgress = (int) ( (MAX_HEALTH - health) / MAX_HEALTH * 9 );
        damageProgress = Math.max(0, Math.min(9, damageProgress)); // Clamp between 0 (no damage) and 9 (max damage)

        // Only send packet if there's actual damage to show or remove
        if (damageProgress > 0 || health == MAX_HEALTH) { // If fully repaired, send -1 to remove
            sendBlockDamagePacket(damageProgress == 0 ? -1 : damageProgress);
        }
    }

    /**
     * Sends a ClientboundBlockDestructionPacket to nearby players to show block crack lines.
     * This method requires NMS access.
     *
     * @param progress The damage progress level (0-9). Use -1 to remove the animation.
     */
    private void sendBlockDamagePacket(int progress) {
        // Ensure the block's chunk is loaded before trying to get its BlockPos
        if (!location.getChunk().isLoaded()) {
            return;
        }

        // Get the NMS BlockPos from the Bukkit Location
        BlockPos nmsBlockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        // Create the block destruction packet


        // Send the packet to all players in the same world who are within rendering distance
        // A common rendering distance for block updates is around 128 blocks (8 chunks * 16 blocks/chunk)
        final double RENDER_DISTANCE_SQUARED = (128.0 * 128.0); // Use squared distance for efficiency

        for (Player bukkitPlayer : location.getWorld().getPlayers()) {
            ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(
                    bukkitPlayer.getEntityId(),
                    nmsBlockPos,
                    progress
            );
            // Only send to players within rendering distance to avoid unnecessary packet sending
            if (bukkitPlayer.getLocation().distanceSquared(location) < RENDER_DISTANCE_SQUARED) {
                // Convert Bukkit Player to NMS ServerPlayer
                CraftPlayer craftPlayer = (CraftPlayer) bukkitPlayer;
                ServerPlayer nmsPlayer = craftPlayer.getHandle();
                // Send the packet
                nmsPlayer.connection.send(packet);
            }
        }
    }
}
