package dev.tom.sentinels.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.entity.HappyGhast;

import java.util.EnumSet;

public class GhastRallyGoal implements Goal<HappyGhast> {

    @Override
    public boolean shouldActivate() {
        return false;
    }

    @Override
    public GoalKey<HappyGhast> getKey() {
        return null;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return null;
    }
}
