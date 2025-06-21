package dev.tom.sentinels.launchable.impl.bomb;

import dev.tom.sentinels.launchable.attributes.Gravity;
import dev.tom.sentinels.launchable.attributes.Knockback;
import dev.tom.sentinels.launchable.attributes.Velocity;
import dev.tom.sentinels.launchable.items.FieldInfo;

import java.io.Serializable;
import java.util.UUID;

public record BombAttributes(
        @FieldInfo(ignore = true) UUID uuid,
        boolean gravity,
        @FieldInfo(name = "speed", unit = "m/s") double velocity,
        double damage,
        @FieldInfo(unit = "m") double radius,
        @FieldInfo(unit = "m/s") double knockback,
        @FieldInfo(name = "") int explosions
) implements Gravity, Velocity, Knockback, Serializable {

}
