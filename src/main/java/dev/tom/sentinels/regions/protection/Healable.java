package dev.tom.sentinels.regions.protection;

public interface Healable {
    /**
     * Gets the current health of the barrier.
     * @return The current health value.
     */
    double getHealth();

    /**
     * Gets the maximum possible health for this barrier.
     * @return The maximum health value.
     */
    double getMaxHealth();

    /**
     * Check if this is max health
     * @return is max health
     */
    boolean isMaxHealth();

    /**
     * Applies damage to the barrier, reducing its health.
     * @param amount The amount of damage to apply.
     * @return The new health value.
     */
    double damage(double amount);

    /**
     * Repairs the barrier, increasing its health. Health will not exceed getMaxHealth().
     * @param amount The amount of health to restore.
     * @return The new health value.
     */
    double repair(double amount);

    /**
     * Restores all health to a barrier
     */
    void fullyRepair();

    /**
     * Set barrier's health
     * @param health to set the barrier to
     */
    void setHealth(double health);

    /**
     * Check if dead
     */
    boolean isDead();
}
