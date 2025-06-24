package dev.tom.sentinels.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import dev.tom.sentinels.Sentinels;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Allay;
import org.bukkit.entity.HappyGhast;

import java.util.EnumSet;

public class GhastRallyGoal implements Goal<HappyGhast> {

    private GoalKey<HappyGhast> key = GoalKey.of(HappyGhast.class, new NamespacedKey(Sentinels.getInstance(), "happy_ghast_rally_goal"));

    private final Location target;
    private final HappyGhast ghast;

    public GhastRallyGoal(HappyGhast ghast, Location flagLocation) {
        this.ghast = ghast;
        this.target = flagLocation.clone().add(0,0,0); // maybe offset?
    }

    @Override
    public void tick() {
        ghast.lookAt(target);
        ghast.getPathfinder().moveTo(target, 5);
        double dist = ghast.getLocation().distance(target);
        System.out.println(dist);
        if(dist <= 1){
            ghast.remove();
            stop();
        }
    }

    @Override
    public boolean shouldActivate() {
        return true;
    }

    @Override
    public GoalKey<HappyGhast> getKey() {
        return key;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.TARGET);
    }
}
