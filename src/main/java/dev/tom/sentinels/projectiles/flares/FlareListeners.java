package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.EntityCollisionEvent;
import dev.tom.sentinels.data.PDCTransferResult;
import dev.tom.sentinels.projectiles.ProjectileManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class FlareListeners implements Listener {

    @EventHandler
    public void playerFireFlare(PlayerInteractEvent e){
        // Only fire when interacting with air
        if(e.getAction() != Action.RIGHT_CLICK_AIR) return;
        if(e.getItem() == null) return;
        if(!e.getItem().getType().equals(Material.RED_CANDLE)) return;
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        // Not a flare, can't fire
        if(!SentinelDataWrapper.getInstance().isType(item.getItemMeta(), FlareAttributes.class)) return;
        Optional<PDCTransferResult<FlareAttributes, FallingBlock>> result = ProjectileManager.getInstance().launchEntity(
                item,
                player,
                FlareAttributes.class
        );
    }

    @EventHandler
    public void entityCollide(EntityCollisionEvent e){
        if(!e.getEntity().isValid()) return;
        Entity entity = e.getEntity();
        if(!SentinelDataWrapper.getInstance().isType(entity, FlareAttributes.class)) return;
        // Flares only
        Optional<FlareAttributes> optionalAttributes = SentinelDataWrapper.getInstance().loadPDC(entity, FlareAttributes.class);
        if(optionalAttributes.isEmpty()) return;
        FlareAttributes attributes = optionalAttributes.get();
        e.getEntity().remove(); // Remove entity now PDC transferred
    }

}
