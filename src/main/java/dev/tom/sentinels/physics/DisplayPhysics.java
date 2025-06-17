package dev.tom.sentinels.physics;

import com.google.common.collect.ImmutableList;
import dev.tom.sentinels.Sentinels;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftBlockDisplay;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DisplayPhysics {

    private final Display display;
    private Vector velocity;

    private static final double GRAVITY_STRENGTH = 0.04;

    private static final double DRAG_COEFFICIENT = 0.98;

    private static final ImmutableList<Direction.Axis> YXZ_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);
    private static final ImmutableList<Direction.Axis> YZX_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X);


    public DisplayPhysics(Display display) {
        this.display = display;
        this.velocity = display.getVelocity();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!display.isValid()) cancel();
                tick();
            }
        }.runTaskTimer(Sentinels.getInstance(), 0, 1);
    }

    public boolean horizontalCollision;
    public boolean verticalCollision;
    public boolean verticalCollisionBelow;
    public boolean minorHorizontalCollision;

    public void tick() {
        if (display.hasGravity()) {
            velocity.add(new Vector(0, -GRAVITY_STRENGTH, 0));
        }
        this.move(new Vec3(velocity.getX(), velocity.getY(), velocity.getZ())); // teleports to new lco
        this.velocity = velocity.clone().multiply(DRAG_COEFFICIENT);
        this.display.setVelocity(this.velocity); // update x/z velo

        if (velocity.lengthSquared() < 0.001 && !display.hasGravity()) { // If very slow and no gravity, consider it stopped
            velocity = new Vector(0, 0, 0); // Zero out velocity
        }

    }

    private void move(Vec3 movement){
        Vec3 vec3 = this.collide(movement);
        double d = vec3.lengthSqr();
        if (d > 1.0E-7) {
            Vec3 vec31 = new Vec3(this.display.getX(), this.display.getY(), this.display.getZ());

            for (Direction.Axis axis : axisStepOrder(vec3)) {
                double d1 = vec3.get(axis);
                if (d1 != 0.0) {
                    Vec3 vec32 = vec31.relative(axis.getPositive(), d1);
                    vec31 = vec32;
                }
            }
            this.display.teleport(new Location(this.display.getWorld(), vec31.x(), vec31.y(), vec31.z()));

        } else {
            display.remove();
        }

        boolean collidedX = !Mth.equal(movement.x, vec3.x);
        boolean collidedY = !Mth.equal(movement.y, vec3.y);
        boolean collidedZ = !Mth.equal(movement.z, vec3.z);

        this.horizontalCollision = collidedX || collidedZ;
        this.verticalCollision = collidedY;
        this.verticalCollisionBelow = this.verticalCollision && movement.y < 0.0;


        Vec3 currentVelocity = this.getVelocityVec3();

        double finalVelX = currentVelocity.x();
        double finalVelY = currentVelocity.y();
        double finalVelZ = currentVelocity.z();

        if (collidedX) {
            finalVelX = 0.0;
        }
        // If there was a Y collision, zero out Y velocity
        if (collidedY) {
            finalVelY = 0.0;
        }
        // If there was a Z collision, zero out Z velocity
        if (collidedZ) {
            finalVelZ = 0.0;
        }

        this.setVelocity(finalVelX, finalVelY, finalVelZ);
    }


    // Paper start - optimise collisions
    protected Vec3 collide(Vec3 movement) {
        final boolean xZero = movement.x == 0.0;
        final boolean yZero = movement.y == 0.0;
        final boolean zZero = movement.z == 0.0;
        if (xZero & yZero & zZero) {
            return movement;
        }

        final AABB currentBox = toAABB(this.display.getBoundingBox());

        final List<VoxelShape> potentialCollisionsVoxel = new ArrayList<>();
        final List<AABB> potentialCollisionsBB = new ArrayList<>();

        final AABB expandedCollisionBox;
        if (xZero & zZero) {
            // note: xZero & zZero -> collision on x/z == 0 -> no step height calculation
            // this specifically optimises entities standing still
            expandedCollisionBox = movement.y < 0.0 ?
                    ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutDownwards(currentBox, movement.y) : ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutUpwards(currentBox, movement.y);
        } else {
            expandedCollisionBox = currentBox.expandTowards(movement);
        }

        Entity rawEntity = ((CraftBlockDisplay) this.display).getHandleRaw();

        Level level = rawEntity.level();

        final List<AABB> entityAABBs = new ArrayList<>();
        ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.getEntityHardCollisions(
                level, rawEntity, expandedCollisionBox, entityAABBs, 0, null
        );

        ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.getCollisionsForBlocksOrWorldBorder(
                level, rawEntity, expandedCollisionBox, potentialCollisionsVoxel, potentialCollisionsBB,
                ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.COLLISION_FLAG_CHECK_BORDER | this.getExtraCollisionFlags(),
                null // Sakura - load chunks on movement
        );
        potentialCollisionsBB.addAll(entityAABBs);
        potentialCollisionsVoxel.forEach(bb -> {
            System.out.print(bb.getCoords(Direction.Axis.X));
        });
        System.out.println("-------------\n");

        return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.performCollisions(movement, currentBox, potentialCollisionsVoxel, potentialCollisionsBB);
    }

    private static Iterable<Direction.Axis> axisStepOrder(Vec3 deltaMovement) {
        return Math.abs(deltaMovement.x) < Math.abs(deltaMovement.z) ? YZX_AXIS_ORDER : YXZ_AXIS_ORDER;
    }

    public int getExtraCollisionFlags() {
        return 0;
    }


    private AABB toAABB(BoundingBox bb) {
        return new AABB(bb.getMinX(), bb.getMinY(), bb.getMinZ(), bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
    }

    public Vec3 getVelocityVec3(){
        return new Vec3(this.getVelocity().getX(), this.getVelocity().getY(), this.getVelocity().getZ());
    }

    public Vec3 toVelocityVec3(Vector vector){
        return new Vec3(vector.getX(), vector.getY(), vector.getZ());
    }

    public Vector toVector(Vec3 vector){
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(double x, double y, double z){
        this.velocity = new Vector(x,y,z);
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
}
