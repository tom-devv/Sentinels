package dev.tom.sentinels.launchable.physics;

import dev.tom.sentinels.Sentinels;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class BasicPhysics {

    private final Display display;
    private Vector velocity;
    private final CollisionDetector collisionDetector;

    private static final double GRAVITY_STRENGTH = 0.04F;

    private static final double DRAG_COEFFICIENT = 0.98F;


    public BasicPhysics(Display display) {
        this.display = display;
        this.velocity = display.getVelocity();
        this.collisionDetector = new CollisionDetector(Sentinels.getInstance(), display);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!display.isValid()) cancel();
                move();
            }
        }.runTaskTimer(Sentinels.getInstance(), 0, 1);
        this.collisionDetector.detect();
    }

    public void move(){
        if (display.hasGravity()) {
            velocity.add(new Vector(0, -GRAVITY_STRENGTH, 0));
        }
        velocity.multiply(DRAG_COEFFICIENT);

        Location loc = display.getLocation();

        Location newLoc = loc.add(velocity);

        display.teleport(newLoc);
        display.setVelocity(velocity);

        if (velocity.lengthSquared() < 0.001 && !display.hasGravity()) { // If very slow and no gravity, consider it stopped
            velocity = new Vector(0, 0, 0); // Zero out velocity
        }
    }


    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
}
