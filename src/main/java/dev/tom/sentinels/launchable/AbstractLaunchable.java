package dev.tom.sentinels.launchable;

import ca.spottedleaf.moonrise.common.util.EntityUtil;
import dev.tom.sentinels.Sentinels;
import dev.tom.sentinels.data.PDCTransferResult;
import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.SentinelProjectileLaunchEvent;
import dev.tom.sentinels.launchable.attributes.Gravity;
import dev.tom.sentinels.launchable.attributes.Knockback;
import dev.tom.sentinels.launchable.attributes.Velocity;
import dev.tom.sentinels.launchable.physics.BasicPhysics;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.text;

public abstract class AbstractLaunchable<T extends Serializable> {

    public static final Map<Entity, AbstractLaunchable<?>> launchables = new HashMap<>();

    protected final ItemStack item;
    protected final BlockData blockData;
    protected final Class<T> type;
    public final T attributes;

    public AbstractLaunchable(ItemStack item, @NotNull BlockData blockData, @NotNull Class<T> type) {
        this.blockData = blockData;
        this.type = type;
        this.item = item;
        Optional<T> opt = SentinelDataWrapper.getInstance().loadPDC(item.getItemMeta(), type);
        if(opt.isPresent()) {
            this.attributes = opt.get();
        } else {
            throw new RuntimeException("Failed to load item PDC on: " + item + " " + type + " " + blockData);
        }
    }

    protected BlockDisplay display;

    /**
     * Launch a display as/from a player
     *
     * @param location where to launch
     * @param player   null unless a player is launching
     * @return optional transfer result
     */
    public final Optional<PDCTransferResult<T, BlockDisplay>> launch(Location location, @Nullable Player player) {
        this.display = createDisplay(location, displaySettings(location));
        if (callEvent()) { // cancelled
            return Optional.empty();
        }

        // we must handle attributes first before physics init
        // because attributes may change entity attributes
        Optional<PDCTransferResult<T, BlockDisplay>> result = handleAttributes(player);
        initPhysics();
        launchables.put(this.display, this);
        return result;
    }

    public final Optional<PDCTransferResult<T, BlockDisplay>> launch(Location location) {
        return launch(location, null);
    }

    protected Optional<PDCTransferResult<T, BlockDisplay>> handleAttributes(Player player) {
        if (display == null) {
            System.err.println("Failed to transfer PDC, BlockDisplay is null");
            System.err.println(this.blockData + " " + this.type + " " + this.item);
            return Optional.empty();
        }
        Optional<PDCTransferResult<T, BlockDisplay>> optionalResult = SentinelDataWrapper.getInstance().transferItemPDC(this.item, display, type);
        if (optionalResult.isPresent()) {
            T attributes = optionalResult.get().data();

            if (attributes instanceof Gravity gravity) {
                display.setGravity(gravity.gravity());
            }
            if (attributes instanceof Velocity velocity) {
                display.setVelocity(display.getVelocity().multiply(velocity.velocity()));
            }
            if (attributes instanceof Knockback knockback && player != null) {
                knockback(player, knockback.knockback());
            }

            return Optional.of(
                    new PDCTransferResult<>(attributes, display)
            );
        } else {
            // Should never fire
            System.err.println("Failed to transfer PDC for ItemStack: " + item);
            return Optional.empty();
        }
    }

     protected static <T extends AbstractLaunchable<?>> void handleLaunch(PlayerInteractEvent e, Class<T> launchableClass, Class<? extends Serializable> attributesClass) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (e.getItem() == null) return;

        ItemStack item = e.getItem();
        Player player = e.getPlayer();

        if (!SentinelDataWrapper.getInstance().isType(item.getItemMeta(), attributesClass)) return;

        try {
            Constructor<T> constructor = launchableClass.getConstructor(ItemStack.class);
            T launchable = constructor.newInstance(item);
            launchable.launch(player.getEyeLocation(), player);
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace(); // or handle gracefully
        }
    }

    protected @NotNull BlockDisplay createDisplay(Location location, final @Nullable Consumer<? super BlockDisplay> function) {
        return location.getWorld().spawn(location, BlockDisplay.class, function);
    }

    protected Consumer<? super BlockDisplay> displaySettings(Location location) {
        Vector direction = location.getDirection();
        return display -> {
            display.setRotation(location.getYaw(), location.getPitch());
            display.setVelocity(direction.normalize());
            display.setTransformation(new Transformation(
                    new Vector3f(-0.5f, -0.5f, -1f),
                    new Quaternionf(),
                    new Vector3f(1, 1, 1),
                    new Quaternionf()
            ));
            display.setBlock(blockData);
            display.setTeleportDuration(2);
            display.setInterpolationDuration(5);
            display.setInvulnerable(true);
        };

    }


    /**
     * @return cancelled
     */
    private boolean callEvent() {
        SentinelProjectileLaunchEvent event = new SentinelProjectileLaunchEvent(this.display);
        Sentinels.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            this.display.remove();
            return true;
        }
        return false;
    }

    private void initPhysics() {
        new BasicPhysics(this.display);
    }

    private void knockback(Player player, double scalar) {
        Entity vehicle;
        if (player.isInsideVehicle()) {
            vehicle = player.getVehicle();
        } else {
            vehicle = player;
        }
        Vector direction = player.getEyeLocation().getDirection().normalize();
        Vector opposite = direction.multiply(-1);
        vehicle.setVelocity(opposite.multiply(scalar));
    }

    /**
     * Remove display entity and key from map
     */
    public void remove() {
        launchables.remove(this.display);
        this.display.remove();
    }


    public T getAttributes() {
        return attributes;
    }

}
