package dev.tom.sentinels.physics;

import com.google.common.collect.ImmutableList;
import dev.tom.sentinels.Sentinels;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.List;

public class DisplayPhysics {

    private static final double GRAVITY_STRENGTH = 0.04;

    private static final double DRAG_COEFFICIENT = 0.98;

    private static final ImmutableList<Direction.Axis> YXZ_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);
    private static final ImmutableList<Direction.Axis> YZX_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X);

    private final Display display;
    private final Entity entity;
    private final Level level;




    public DisplayPhysics(Display display) {
        this.display = display;
        this.entity = ((CraftEntity) this.display).getHandleRaw();
        this.level = this.entity.level();
        this.velocity = display.getVelocity();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!display.isValid()) cancel();
                tick();
            }
        }.runTaskTimer(Sentinels.getInstance(), 0, 1);
    }

    private Vector velocity;

    public void tick() {
        if (display.hasGravity()) {
            velocity.add(new Vector(0, -GRAVITY_STRENGTH, 0));
        }
        this.move(toVec3(this.velocity));
        this.setVelocity(this.velocity.multiply(DRAG_COEFFICIENT));
        this.display.setVelocity(this.velocity);
    }

    private void move(Vec3 movement){
        Vec3 collision = this.collide(movement);
        double collisionSpeedSquare = collision.lengthSqr();
        if(collisionSpeedSquare > 1.0E-7 && movement.lengthSqr() - collisionSpeedSquare < 1.0E-7) {
            Vec3 position = entity.position();

            for (Direction.Axis axis : axisStepOrder(collision)) {
                double d1 = collision.get(axis);
                if (d1 != 0.0) {
                    position = position.relative(axis.getPositive(), d1);
                }
            }
            entity.setPosRaw(position.x, position.y, position.z, true);
            System.out.println(entity.getBoundingBox().getCenter());
            this.display.teleport(new Location(this.display.getWorld(), position.x, position.y, position.z));
        }
        boolean xBlocked = movement.x != collision.x;
        boolean yBlocked = movement.y != collision.y;
        boolean zBlocked = movement.z != collision.z;

        double newVx = xBlocked ? 0 : this.velocity.getX();
        double newVy = yBlocked ? 0 : this.velocity.getY();
        double newVz = zBlocked ? 0 : this.velocity.getZ();

        this.setVelocity(newVx, newVy, newVz);
    }



    // Paper start - optimise collisions
    protected Vec3 collide(Vec3 movement) {
        // no movement so no collisions
        if (movement.x == 0.0 && movement.y == 0.0 && movement.z == 0.0) {
            return movement;
        }

        List<VoxelShape> potentialCollisionsVoxel = new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(0);
        List<AABB> potentialCollisionsBB = new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(4);

        AABB currBoundingBox = toAABB(this.display.getBoundingBox());

        return this.collideAxis(movement, currBoundingBox, potentialCollisionsVoxel, potentialCollisionsBB);
    }

    private Vec3 collideAxis(Vec3 movement, AABB currBoundingBox, List<VoxelShape> voxelList, List<AABB> bbList){
        double x = movement.x;
        double y = movement.y;
        double z = movement.z;

        boolean xFirst = !(Math.abs(x) < Math.abs(z)); // Move in the biggest direction first

        if(y != 0){
            y = this.collideY(currBoundingBox, y, voxelList, bbList);
            if(y != 0){ // Some vertical movement is possible!
                // Actually shift the bounding box because the entity has MOVED to this new position
                currBoundingBox = ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.offsetY(currBoundingBox, y);
            }
        }
        // Z -> X
        if(!xFirst && z != 0){
            z = this.collideZ(currBoundingBox, z, voxelList, bbList);
            if(z != 0){
                 currBoundingBox = ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.offsetZ(currBoundingBox, z);
            }
        }

        if(x != 0) {
            x = this.collideX(currBoundingBox, x, voxelList, bbList);
            if(x != 0) {
                currBoundingBox = ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.offsetX(currBoundingBox, x);
            }
        }
        if(!xFirst && z != 0){
            z = this.collideZ(currBoundingBox, z, voxelList, bbList);
        }

        return new Vec3(x,y,z);
    }

    private double collideY(AABB currBoundingBox, double y, List<VoxelShape> voxelList, List<AABB> bbList){
        // Expand bb upwards to check for voxels
        AABB expandedScanYBox = cutBoundingBoxY(currBoundingBox, y);
        this.collectCollisions(expandedScanYBox, voxelList, bbList, 0);
        y = ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.performAABBCollisionsY(currBoundingBox, y, bbList);
        return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.performVoxelCollisionsY(currBoundingBox, y, voxelList);
    }

    private double collideX(AABB currBoundingBox, double x, List<VoxelShape> voxelList, List<AABB> bbList){
        // Expand bb upwards to check for voxels
        AABB expandedScanXBox = cutBoundingBoxX(currBoundingBox, x);
        this.collectCollisions(expandedScanXBox, voxelList, bbList, 0);
        x = ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.performAABBCollisionsX(currBoundingBox, x, bbList);
        return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.performVoxelCollisionsX(currBoundingBox, x, voxelList);
    }

    private double collideZ(AABB currBoundingBox, double z, List<VoxelShape> voxelList, List<AABB> bbList){
        AABB expandedScanZBox = cutBoundingBoxZ(currBoundingBox, z);
        this.collectCollisions(expandedScanZBox, voxelList, bbList, 0);
        z = ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.performAABBCollisionsZ(currBoundingBox, z, bbList);
        return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.performVoxelCollisionsX(currBoundingBox, z, voxelList);
    }

    private void collectCollisions(AABB collisionBox, List<VoxelShape> voxelList, List<AABB> bbList, int flags) {
        // Copied from the collide method below
        ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.getCollisionsForBlocksOrWorldBorder(
                this.level, this.entity, collisionBox, voxelList, bbList,
                flags  | this.getExtraCollisionFlags(), null
        );

        ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.getEntityHardCollisions(
                this.level, this.entity, collisionBox, bbList, 0, null
        );
    }

    private static Iterable<Direction.Axis> axisStepOrder(Vec3 deltaMovement) {
        return Math.abs(deltaMovement.x) < Math.abs(deltaMovement.z) ? YZX_AXIS_ORDER : YXZ_AXIS_ORDER;
    }

    private static AABB cutBoundingBoxX(AABB bb, double x) {
        if (x > 0.0) {
            return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutRight(bb, x);
        } else {
            return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutLeft(bb, x);
        }
    }

    private static AABB cutBoundingBoxY(AABB bb, double y) {
        if (y > 0.0) {
            return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutUpwards(bb, y);
        } else {
            return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutDownwards(bb, y);
        }
    }

    private static AABB cutBoundingBoxZ(AABB bb, double z) {
        if (z > 0.0) {
            return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutForwards(bb, z);
        } else {
            return ca.spottedleaf.moonrise.patches.collisions.CollisionUtil.cutBackwards(bb, z);
        }
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

    public Vec3 toVec3(Vector vector){
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
