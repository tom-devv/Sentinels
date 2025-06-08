package dev.tom.sentinels.listeners;

import dev.tom.sentinels.data.FlareAttributes;
import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.projectiles.PDCTransferResult;
import dev.tom.sentinels.projectiles.ProjectileManager;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class FlareListeners implements Listener {

    @EventHandler
    public void playerFireFlare(PlayerInteractEvent e){
        // Only fire when interacting with air
        if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if(e.getItem() == null) return;
        if(!e.getItem().getType().equals(Material.RED_CANDLE)) return;
        Player player = e.getPlayer();
        FlareAttributes attributes = new FlareAttributes(false, 10, 11, 12);
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        SentinelDataWrapper.getInstance().savePDC(meta, attributes);
        item.setItemMeta(meta);
        Optional<PDCTransferResult<FlareAttributes, Display>> result = ProjectileManager.getInstance().launchEntity(
                item,
                player,
                FlareAttributes.class
        );

    }

}
