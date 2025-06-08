package dev.tom.sentinels.regions.impl;

import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.Healable;
import dev.tom.sentinels.regions.protection.Protected;
import dev.tom.sentinels.regions.protection.StandardBarrier;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class ProtectedCuboidRegion extends CuboidRegion implements Protected, Healable {

    private final Set<Barrier> barriers = new HashSet<>();

    public ProtectedCuboidRegion(String name,Location point1, Location point2) {
        super(name, point1, point2);
        for (Location location : getVerticalFaces()) {
            barriers.add(new StandardBarrier(this, location));
        }
    }

    @Override
    public Set<Barrier> getBarriers() {
        return this.barriers;
    }

    @Override
    public double getHealth() { // Changed to double
        double health = 0.0; // Changed to double
        for (Barrier barrier : barriers) {
            health += barrier.getHealth();
        }
        return health;
    }

    @Override
    public double getMaxHealth() { // Changed to double
        double health = 0.0; // Changed to double
        for (Barrier barrier : barriers) {
            health += barrier.getMaxHealth();
        }
        return health;
    }

    @Override
    public double damage(double amount) {
        if (barriers.isEmpty() || amount <= 0.0) return getHealth();

        double damagePerBarrier = amount / barriers.size();

        for (Barrier barrier : barriers) {
            barrier.damage(damagePerBarrier);
        }
        return getHealth();
    }

    @Override
    public double repair(double amount) {
        if (barriers.isEmpty() || amount <= 0.0) return getHealth();

        double repairPerBarrier = amount / barriers.size();

        for (Barrier barrier : barriers) {
            barrier.repair(repairPerBarrier);
        }
        return getHealth();
    }

    @Override
    public void fullyRepair() {
        getBarriers().forEach(Healable::fullyRepair);
    }

    /**
     * Sets the total health of the ProtectedCuboidRegion to the specified value
     * by distributing this health proportionally across all its individual barriers.
     * The input 'health' will be clamped between 0 and the region's total maximum health.
     * Each barrier's individual health will also be clamped between its own 0 and max health.
     *
     * IMPORTANT: This method assumes that the 'Barrier' interface (e.g., RegionBarrier)
     * and its implementations (e.g., StandardBarrier) have a 'void setHealth(int newHealth)' method.
     *
     * @param health The desired total health for the region.
     */
    @Override
    public void setHealth(double health) {
        if (barriers.isEmpty()) {
            return;
        }

        // Clamp the desired total health to the region's overall min/max
        double totalMaxHealth = getMaxHealth();
        double clampedDesiredTotalHealth = Math.max(0, Math.min(health, totalMaxHealth));

        // Calculate the base health to distribute evenly among all barriers
        double baseHealthPerBarrier = clampedDesiredTotalHealth / barriers.size();
        // Calculate the remainder that needs to be distributed to a few barriers
        double remainder = clampedDesiredTotalHealth % barriers.size();

        // Iterate through all barriers and set their health
        int count = 0;
        for (Barrier barrier : barriers) {
            double currentBarrierTargetHealth = baseHealthPerBarrier;
            if (count < remainder) {
                currentBarrierTargetHealth++; // Distribute the remainder
            }

            // The individual barrier's setHealth method should handle clamping
            // its health to its own min (0) and max health.
            barrier.setHealth(currentBarrierTargetHealth);
            count++;
        }
    }

    @Override
    public boolean isDead() {
        boolean dead = true;
        for (Barrier barrier : getBarriers()) {
            if(!barrier.isDead()) dead = false;
        }
        return dead || getHealth() < 0;
    }
}
