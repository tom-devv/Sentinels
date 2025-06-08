package dev.tom.sentinels.regions;

import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegionManager implements Manager<Region> {

    private static RegionManager INSTANCE;

    public static RegionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RegionManager();
        }
        return INSTANCE;
    }
    private RegionManager() {}

    Set<Region> regions = new HashSet<>();
    Map<World, Set<Region>> worldRegions = new HashMap<>();

    @Override
    public Set<Region> getAll() {
        return regions;
    }

    @Override
    public void add(Region region) {
        regions.add(region);
        worldRegions.computeIfAbsent(region.getWorld(), k -> new HashSet<>()).add(region);
    }

    @Override
    public void remove(Region region) {
        regions.remove(region);
        worldRegions.computeIfAbsent(region.getWorld(), k -> new HashSet<>()).remove(region);
    }
}
