package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.projectiles.Gravity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FlareAttributes implements Gravity, java.io.Serializable {

    private UUID firedBy;
    private int mobCount;
    private double mobHealth;
    private double healingPerTick;
    private boolean gravity;
    private int searchRadius;

    public FlareAttributes(Player player, boolean gravity, double healingPerTick, double mobHealth, int mobCount, int searchRadius) {
        this.firedBy = player.getUniqueId();
        this.gravity = gravity;
        this.healingPerTick = healingPerTick;
        this.mobHealth = mobHealth;
        this.mobCount = mobCount;
        this.searchRadius = searchRadius;
    }

    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }

    public int getSearchRadius() {
        return searchRadius;
    }

    public UUID getFiredBy() {
        return firedBy;
    }

    public void setFiredBy(UUID firedBy) {
        this.firedBy = firedBy;
    }

    public double getHealingPerTick() {
        return healingPerTick;
    }

    public void setHealingPerTick(double healingPerTick) {
        this.healingPerTick = healingPerTick;
    }

    public int getMobCount() {
        return mobCount;
    }

    public void setMobCount(int mobCount) {
        this.mobCount = mobCount;
    }

    public double getMobHealth() {
        return mobHealth;
    }

    public void setMobHealth(double mobHealth) {
        this.mobHealth = mobHealth;
    }

    @Override
    public void setGravity(boolean b) {
        this.gravity = b;
    }

    @Override
    public boolean hasGravity() {
        return this.gravity;
    }
}
