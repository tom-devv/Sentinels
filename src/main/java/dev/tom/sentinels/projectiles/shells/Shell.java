package dev.tom.sentinels.projectiles.shells;

import dev.tom.sentinels.projectiles.AttributeLaunchable;
import dev.tom.sentinels.projectiles.LaunchableListener;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Serializable;

public class Shell extends AttributeLaunchable<ShellAttributes> implements LaunchableListener {

    public Shell(ItemStack item, BlockData blockData, Class<ShellAttributes> type) {
        super(item, blockData, type);
    }

    @Override
    public void registerListener(JavaPlugin plugin) {

    }
}
