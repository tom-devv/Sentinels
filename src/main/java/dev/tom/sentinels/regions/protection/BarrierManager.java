package dev.tom.sentinels.regions.protection;

import dev.tom.sentinels.regions.Manager;
import dev.tom.sentinels.regions.Region;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BarrierManager implements Manager<Barrier>  {

    private static BarrierManager INSTANCE = new BarrierManager();

    public static BarrierManager getInstance() {
        if(INSTANCE == null) INSTANCE = new BarrierManager();
        return INSTANCE;
    }

    private BarrierManager() {};

    public Set<Barrier> barriers = new HashSet<>();
    public Map<Location, Region> locationRegionMap = new HashMap<>();
    public Map<Location, Barrier> locationBarrierMap = new HashMap<>();

    @Override
    public Set<Barrier> getAll() {
        return barriers;
    }

    @Override
    public void add(Barrier barrier) {
        barriers.add(barrier);
        locationRegionMap.put(barrier.getLocation(), barrier.getRegion());
        locationBarrierMap.put(barrier.getLocation(), barrier);
    }

    @Override
    public void remove(Barrier barrier) {
        barriers.remove(barrier);
        locationRegionMap.remove(barrier.getLocation());
        locationBarrierMap.remove(barrier.getLocation());
    }

    public Barrier getBarrier(Block block){
        return getBarrier(block.getLocation());
    }

    public Barrier getBarrier(Location location){
        return locationBarrierMap.get(location);
    }
}
