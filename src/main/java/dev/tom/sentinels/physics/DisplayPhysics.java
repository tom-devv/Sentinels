package dev.tom.sentinels.physics;

import dev.tom.sentinels.Sentinels;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DisplayPhysics {

    private final Display display;
    private Vector mot;

    private static final double GRAVITY_STRENGTH = -0.04F;

    private static final double DRAG_COEFFICIENT = 0.98F;


    public DisplayPhysics(Display display) {
        this.display = display;
        this.mot = display.getVelocity();
        System.out.println(mot);
        new BukkitRunnable(){
            @Override
            public void run() {
                if(!display.isValid()) cancel();
                move();
            }
        }.runTaskTimer(Sentinels.getInstance(), 0, 1);
    }

    public void move(){
        if (display.hasGravity()) {
            // Gravity acts downwards along the Y-axis
            mot.add(new Vector(0, GRAVITY_STRENGTH, 0));
        }
        mot.multiply(DRAG_COEFFICIENT);

        Location loc = display.getLocation();

        Location newLoc = loc.add(mot);

        display.teleport(newLoc);

        if (mot.lengthSquared() < 0.001 && !display.hasGravity()) { // If very slow and no gravity, consider it stopped
            mot = new Vector(0, 0, 0); // Zero out velocity
        }
    }

    public Vector getMot() {
        return mot;
    }

    public void setMot(Vector mot) {
        this.mot = mot;
    }
}
