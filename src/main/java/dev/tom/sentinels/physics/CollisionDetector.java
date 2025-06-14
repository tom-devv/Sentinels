package dev.tom.sentinels.physics;

import dev.tom.sentinels.events.EntityCollisionEvent;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;


public class CollisionDetector {


    private static final int NUM_RAYS = 64;
    private static final double RAY_DISTANCE = 0.7;

    static Vector[] rayDirections = new Vector[NUM_RAYS];

    static {
        for (int i = 0; i < NUM_RAYS; i++) {
            double angleDegrees = i * (360.0 / NUM_RAYS);
            double angleRadians = Math.toRadians(angleDegrees);
            rayDirections[i] = new Vector(Math.cos(angleRadians), 0, Math.sin(angleRadians)).normalize();
        }
    }

    private static Map<Entity, CollisionDetector> trackedEntities = new HashMap<>();

    private final Entity entity;
    private final JavaPlugin plugin;
    private BukkitTask collisionTask;

    public CollisionDetector(JavaPlugin plugin, Entity entity){
        this.entity = entity;
        this.plugin = plugin;
        trackedEntities.put(entity, this);
    }

    public BukkitTask detect(){
        this.collisionTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(!entity.isValid()) {
                    stopCollisionTask();
                }
                for (int i = 0; i < rayDirections.length; i++) {
                    Vector rayDirection = rayDirections[i];
                    Location centerLocation = new Location(entity.getWorld(), entity.getBoundingBox().getCenterX(), entity.getBoundingBox().getCenterY(), entity.getBoundingBox().getCenterZ());
                    RayTraceResult trace = entity.getWorld().rayTraceBlocks(
                            centerLocation,
                            rayDirection,
                            RAY_DISTANCE,
                            FluidCollisionMode.NEVER,
                            true
                    );
                    if(trace == null || trace.getHitBlock() == null) continue;

                    Block block = trace.getHitBlock();
                    EntityCollisionEvent event = new EntityCollisionEvent(entity, block, trace.getHitBlockFace());
                    plugin.getServer().getPluginManager().callEvent(event);
                    stopCollisionTask();
                    return;
                }
            }
        }.runTaskTimer(this.plugin, 0, 1);
        return collisionTask;
    }

    private void stopCollisionTask() {
        trackedEntities.remove(entity);
        if(this.collisionTask != null) {
            if (!this.collisionTask.isCancelled()) {
                this.collisionTask.cancel();
            }
        }
    }
}
