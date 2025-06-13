package dev.tom.sentinels.projectiles.shells;

import dev.tom.sentinels.projectiles.Gravity;

import java.io.Serializable;

public record ShellAttributes(
        double damage,
        boolean gravity,
        double radius
) implements Gravity, Serializable {

    @Override
    public boolean gravity() {
        return false;
    }
}
