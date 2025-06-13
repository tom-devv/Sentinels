package dev.tom.sentinels.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.regions.RegionManager;
import dev.tom.sentinels.regions.protection.BarrierManager;
import dev.tom.sentinels.utils.BlockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Allay;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class AllayRepairGoal implements Goal<Allay> {

    private GoalKey<Allay> key = GoalKey.of(Allay.class, new NamespacedKey(Sentinels.getInstance(), "flying_mob"));
    private final Allay allay;
    private final Location initBlock;
    private final int radius;

    public AllayRepairGoal(Allay mob, Location initBlock, int radius) {
        this.allay = mob;
        this.initBlock = initBlock;
        this.radius = radius;
    }

    public Block findABrokenBlock() {
        BarrierManager manager = BarrierManager.getInstance();
        for (Block block : BlockUtil.getBlocksInRadius(initBlock, radius)) {

            manager.getBarrier(block.getLocation());
        }
    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public void tick() {
//        allay.lookAt(targetBlock);
//        allay.getPathfinder().moveTo(targetBlock);
//        if(allay.getLocation().distance(targetBlock) < 1.5 && !allay.isDancing()) {
//            initBlock.getBlock().setType(Material.GLOWSTONE);
//            allay.startDancing();
//        }
    }

    @Override
    public GoalKey<Allay> getKey() {
        return key;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }
}
