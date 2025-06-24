package dev.tom.sentinels.items;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ItemListenerRegister {

    static Reflections items = new Reflections("dev.tom.sentinels");

    public static void register(JavaPlugin plugin) {
            Set<Class<? extends ItemListener>> listeners = items.getSubTypesOf(ItemListener.class);
            listeners.forEach(listener -> {
                try {
                    Constructor<? extends ItemListener> constructor = listener.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Bukkit.getServer().getPluginManager().registerEvents(constructor.newInstance(), plugin);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
