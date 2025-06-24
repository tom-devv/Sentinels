package dev.tom.sentinels.items;

import dev.tom.sentinels.launchables.Launchable;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

import java.util.Optional;

/**
 * Just for reflection to autoregister launchable listeners
 */
public interface ItemListener<T extends Item<?>> extends Listener {


    // TODO move this elsewhere
    @SuppressWarnings("unchecked")
    default Optional<T> getLaunchable(Entity entity) {
        Launchable<?> launchable = Launchable.launchables.get(entity);
        if (launchable != null) {
            try {
                return Optional.of((T) launchable);
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
