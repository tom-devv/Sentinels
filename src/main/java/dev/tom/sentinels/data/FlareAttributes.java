package dev.tom.sentinels.data;

public class FlareAttributes implements Gravity, java.io.Serializable {
    private int mobCount;
    private int mobHealth;
    private double healingPerTick;
    private boolean gravity;

    public FlareAttributes(boolean gravity, double healingPerTick, int mobHealth, int mobCount) {
        this.gravity = gravity;
        this.healingPerTick = healingPerTick;
        this.mobHealth = mobHealth;
        this.mobCount = mobCount;
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

    public int getMobHealth() {
        return mobHealth;
    }

    public void setMobHealth(int mobHealth) {
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
