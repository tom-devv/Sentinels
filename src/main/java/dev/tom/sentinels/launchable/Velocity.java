package dev.tom.sentinels.launchable;


public interface Velocity {

    /**
     * No more than 1.5, beyond this causes clipping issues with rays
     * @return velocity multiplier
     */
    public double velocity();
}
