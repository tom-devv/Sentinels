package dev.tom.sentinels.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.BarrierManager;
import dev.tom.sentinels.utils.BlockUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Allay;

import java.util.*;

public class AllayRepairGoal implements Goal<Allay> {

    private GoalKey<Allay> key = GoalKey.of(Allay.class, new NamespacedKey(Sentinels.getInstance(), "barrier_repair_goal"));
    private final Allay allay;
    private final Location initBlock;
    private final int radius;
    private final double healingPerTick;

    public AllayRepairGoal(Allay mob, Location initBlock, int radius, double healingPerTick) {
        this.allay = mob;
        this.initBlock = initBlock;
        this.radius = radius;
        this.healingPerTick = healingPerTick;
    }

    private Goal goal = null;
    private Optional<Barrier> findClosestDamagedBarrier() {
        BarrierManager manager = BarrierManager.getInstance();
        List<Barrier> brokenBarriers = new ArrayList<>();
        for (Block block : BlockUtil.getBlocksInRadius(initBlock, radius)) {
            Barrier barrier = manager.getBarrier(block.getLocation());
            if (barrier == null || barrier.isDead() || barrier.isMaxHealth()) continue;
            brokenBarriers.add(barrier);
        }
        if (brokenBarriers.isEmpty()) return Optional.empty();
        return brokenBarriers.stream().min(Comparator.comparingDouble(barrier -> barrier.getLocation().distance(allay.getLocation())));
    }

    /**
     * MoveTo location should be adjacent
     * @param barrier
     * @return optional adjacent location
     */
    private Optional<Location> findMoveTo(Barrier barrier) {
        Set<BlockFace> faces = barrier.getRegion().getOutwardFaces(barrier.getLocation());
        if(faces.isEmpty()) return Optional.empty();
        Location adjacent;
        Optional<BlockFace> face = faces.stream().findAny();
        adjacent = barrier.getLocation().clone().add(face.get().getDirection());
        return Optional.of(adjacent);
    }


    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public void tick() {
        // Find a new block to heal
        if(goal == null || goal.barrier().isDead() || goal.barrier().isMaxHealth()) {
            findClosestDamagedBarrier().ifPresent(barrier -> {
                findMoveTo(barrier).ifPresent(moveTo -> {
                    this.goal = new Goal(barrier, moveTo);
                });
            });
            if(findClosestDamagedBarrier().isEmpty()) {
                despawn();
                return;
            }
        }
        // currentTargetBarrier is now a damaged barrier
        Block block = goal.target().getBlock();
        allay.lookAt(block.getLocation());
        allay.getPathfinder().moveTo(block.getLocation());
        double dist = allay.getLocation().distance(block.getLocation());
        if(dist <= 1){
            goal.barrier().repair(healingPerTick);
            block.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, goal.barrier().getLocation(), 3, 0.3,0.5,0.3);
        }
    }

    public void despawn(){
        allay.getWorld().spawnParticle(Particle.SCULK_SOUL, allay.getLocation(), 2, 0.2, 0, 0.2,0);
        allay.remove();
        stop();
    }

    @Override
    public GoalKey<Allay> getKey() {
        return key;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }


    private record Goal(Barrier barrier, Location target){};
    }
