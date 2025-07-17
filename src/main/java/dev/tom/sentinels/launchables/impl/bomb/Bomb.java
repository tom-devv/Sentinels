package dev.tom.sentinels.launchables.impl.bomb;

import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.launchables.Launchable;
import dev.tom.sentinels.items.ItemListener;
import dev.tom.sentinels.regions.protection.Healable;
import dev.tom.sentinels.utils.BlockUtil;
import dev.tom.sentinels.utils.RegionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Set;
import java.util.function.Consumer;

public class Bomb extends Launchable<BombAttributes> {

    public Bomb(ItemStack item) {
        super(item, Material.MAGMA_BLOCK.createBlockData(), BombAttributes.class);
    }

    private static final long TIME_BETWEEN_EXPLOSIONS = (long) (1.5*20);

    /**
     * Find nearby healable regions and damage them entirely
     */
    public void explode() {
        Set<Healable> healableRegions = RegionUtil.getHealableRegions(
                BlockUtil.getBlocksInRadius(this.display.getLocation(), attributes.radius())
        );
        new BukkitRunnable() {
            int i = attributes.explosions();
            @Override
            public void run() {
                i--;
                if(i <= 0) cancel();
                int count = 3;
                spawnParticle(display, Particle.EXPLOSION, count);
                spawnParticle(display, Particle.FIREWORK, 5);
                for (Healable healable : healableRegions) {
                    healable.damage(attributes.damage());
                }
            }
        }.runTaskTimer(Sentinels.getInstance(), 0, TIME_BETWEEN_EXPLOSIONS);
    }

    private static void spawnParticle(Entity entity, Particle particle, int count) {
        entity.getWorld().spawnParticle(particle, entity.getLocation(), count, 0.1, 0.1, 0.1);
    }

    @Override
    protected Consumer<? super BlockDisplay> displaySettings(Location location) {
        Vector direction = location.getDirection();
        return display -> {
            display.setRotation(location.getYaw(), location.getPitch());
            display.setVelocity(direction.normalize());
            display.setTransformation(new Transformation(
                    new Vector3f(-0.5f, -0.5f, -1f),
                    new Quaternionf(),
                    new Vector3f(3, 3, 3),
                    new Quaternionf()
            ));
            display.setBlock(blockData);
            display.setTeleportDuration(2);
            display.setInterpolationDuration(5);
            display.setInvulnerable(true);
        };
    }

    private static class BombListeners implements ItemListener<Bomb> {

        @EventHandler
        public void bombCollide(SentinelProjectileCollideEvent e) {
            Entity entity = e.getEntity();
            getLaunchable(entity).ifPresent(bomb -> {
                bomb.explode();
                bomb.remove();
            });
        }


        @EventHandler
        public void playerFireBomb(PlayerInteractEvent e){
            handleLaunch(e, Bomb.class, BombAttributes.class);
        }
    }
}
