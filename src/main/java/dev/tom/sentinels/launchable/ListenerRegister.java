package dev.tom.sentinels.launchable;

import joptsimple.internal.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ListenerRegister {

    static Reflections launchables = new Reflections("dev.tom.sentinels.launchable.impl");

    public static void register(JavaPlugin plugin) {
            Set<Class<? extends LaunchableListener>> listeners = launchables.getSubTypesOf(LaunchableListener.class);
            listeners.forEach(listener -> {
                try {
                    Constructor<? extends LaunchableListener> constructor = listener.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Bukkit.getServer().getPluginManager().registerEvents(constructor.newInstance(), plugin);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
