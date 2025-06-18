package dev.tom.sentinels;

import dev.tom.sentinels.commands.ProjectileCommand;
import dev.tom.sentinels.regions.impl.ProtectedCuboidRegion;
import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.BarrierManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Sentinels extends JavaPlugin implements Listener {


    public static Sentinels instance;

    @Override
    public void onEnable() {
        instance = this;
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(ProjectileCommand.command());
        });
        getServer().getPluginManager().registerEvents(this, this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            spawnCube();
        }, 20 * 3);
    }

    public void spawnCube() {
        World world = Bukkit.getWorld("pancake");
        System.out.println(world);
        Location location = new Location(world, 100, -60, -100);
        ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(
                "test-protection",
                location,
                location.clone().add(20, 20, 20)
        );
        protectedCuboidRegion.fullyRepair();
    }

    @EventHandler
    public void interact(PlayerInteractEvent e){
        if(!e.getAction().isRightClick()) return;
        Block block = e.getClickedBlock();
        if(block == null) return;
        Barrier barrier = BarrierManager.getInstance().getBarrier(block.getLocation());
        if(barrier == null) {
            System.out.println("Null barrier");
            return;
        }
        e.getPlayer().sendMessage("Barrier health: " + barrier.getHealth());
    }


    public static Sentinels getInstance() {
        return instance;
    }
}
