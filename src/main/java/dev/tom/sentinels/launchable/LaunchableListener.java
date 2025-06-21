package dev.tom.sentinels.launchable;

import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

import java.io.Serializable;
import java.util.Optional;

/**
 * Just for reflection to autoregister launchable listeners
 */
public interface LaunchableListener<T extends AbstractLaunchable<?>> extends Listener {

    @SuppressWarnings("unchecked")
    default Optional<T> getLaunchable(Entity entity) {
        AbstractLaunchable<?> launchable = AbstractLaunchable.launchables.get(entity);
        if (launchable != null) {
            try {
                return Optional.of((T) launchable); // Unsafe but necessary due to type erasure
            } catch (ClassCastException e) {
                return Optional.empty(); // Type mismatch
            }
        }
        return Optional.empty();
    }
}
