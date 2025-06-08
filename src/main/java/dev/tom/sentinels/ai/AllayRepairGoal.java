package dev.tom.sentinels.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.tom.sentinels.Sentinels;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Allay;

import java.util.EnumSet;

public class AllayRepairGoal implements Goal<Allay> {

    private GoalKey<Allay> key = GoalKey.of(Allay.class, new NamespacedKey(Sentinels.getInstance(), "flying_mob"));
    private final Allay allay;
    private final Location targetBlock;
    private final Location fixBlock;

    public AllayRepairGoal(Allay mob, Location targetBlock, Location fixBlock) {
        this.allay = mob;
        this.targetBlock = targetBlock;
        this.fixBlock = fixBlock;
    }

    private void findLocation() {

    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public void tick() {
        allay.lookAt(targetBlock);
        allay.getPathfinder().moveTo(targetBlock);
        if(allay.getLocation().distance(targetBlock) < 1.5 && !allay.isDancing()) {
            fixBlock.getBlock().setType(Material.GLOWSTONE);
            allay.startDancing();
        }
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
