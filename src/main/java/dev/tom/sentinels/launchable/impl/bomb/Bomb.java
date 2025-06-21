package dev.tom.sentinels.launchable.impl.bomb;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.SentinelProjectileCollideBarrierEvent;
import dev.tom.sentinels.events.SentinelProjectileCollideEvent;
import dev.tom.sentinels.launchable.AbstractLaunchable;
import dev.tom.sentinels.launchable.LaunchableListener;
import dev.tom.sentinels.launchable.impl.flares.Flare;
import dev.tom.sentinels.launchable.impl.flares.FlareAttributes;
import dev.tom.sentinels.regions.Region;
import dev.tom.sentinels.regions.impl.ProtectedCuboidRegion;
import dev.tom.sentinels.regions.protection.Barrier;
import dev.tom.sentinels.regions.protection.BarrierManager;
import dev.tom.sentinels.regions.protection.Healable;
import dev.tom.sentinels.utils.BlockUtil;
import dev.tom.sentinels.utils.RegionUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class Bomb extends AbstractLaunchable<BombAttributes> {

    public Bomb(ItemStack item) {
        super(item, Material.SHULKER_SHELL.createBlockData(), BombAttributes.class);
    }

    /**
     * Find nearby healable regions and damage them entirely
     * @param entity
     * @param attributes
     */
    public static void explode(Entity entity, BombAttributes attributes) {
        spawnParticle(entity, Particle.EXPLOSION, 3);
        spawnParticle(entity, Particle.FIREWORK, 5);
        Set<Healable> healableRegions = RegionUtil.getHealableRegions(
                BlockUtil.getBlocksInRadius(entity.getLocation(), attributes.radius())
        );
        for (Healable healable : healableRegions) {
            healable.damage(attributes.damage());
        }
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

    private static class BombListeners implements LaunchableListener {

        @EventHandler
        public void bombCollide(SentinelProjectileCollideEvent e) {
            Entity entity = e.getEntity();
            Optional<BombAttributes> optionalAttributes;
            if((optionalAttributes = SentinelDataWrapper.getInstance().loadPDC(entity, BombAttributes.class)).isEmpty()) return;
            BombAttributes attributes = optionalAttributes.get();
            for (int i = 0; i < attributes.explosions(); i++) {
                Bomb.explode(entity, attributes);
            }
            entity.remove();
        }


        @EventHandler
        public void playerFireFlare(PlayerInteractEvent e){
            handleLaunch(e, Bomb.class, BombAttributes.class);
        }
    }
}
