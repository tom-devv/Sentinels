package dev.tom.sentinels.data;

import java.io.Serializable;

public class DamagingAttributes implements Serializable, Gravity {
    private static final long serialVersionUID = 1L; // Recommended

    private double damage;
    private double radius;
    private boolean gravity;

    public DamagingAttributes(double damage, boolean gravity, double radius) {
        this.damage = damage;
        this.gravity = gravity;
        this.radius = radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public boolean hasGravity() {
        return gravity;
    }

    @Override
    public void setGravity(boolean gravity) {
        this.gravity = gravity;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
