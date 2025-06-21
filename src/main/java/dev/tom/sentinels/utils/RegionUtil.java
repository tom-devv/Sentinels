package dev.tom.sentinels.utils;

import dev.tom.sentinels.regions.Region;
import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.BarrierManager;
import dev.tom.sentinels.regions.protection.Healable;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegionUtil {

    public static Set<Healable> getHealableRegions(Set<Block> blocks) {
        Set<Healable> effectedHealableRegions = new HashSet<>();
        BarrierManager manager = BarrierManager.getInstance();
        for (Block block : blocks) {
            Barrier barrier = manager.getBarrier(block.getLocation());
            if(barrier == null) continue;
            if(barrier.getRegion() instanceof Healable healable) {
                effectedHealableRegions.add(healable);
            }
        }
        return effectedHealableRegions;
    }
}
