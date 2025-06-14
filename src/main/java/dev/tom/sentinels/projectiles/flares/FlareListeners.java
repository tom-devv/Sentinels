package dev.tom.sentinels.projectiles.flares;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.events.EntityCollisionEvent;
import dev.tom.sentinels.data.PDCTransferResult;
import dev.tom.sentinels.physics.PhysicsManager;
import dev.tom.sentinels.utils.MobCreator;
import org.bukkit.block.Block;
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
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if(!SentinelDataWrapper.getInstance().isType(item.getItemMeta(), FlareAttributes.class)) return;
        // Not a flare, can't fire
        Optional<PDCTransferResult<FlareAttributes, FallingBlock>> result = PhysicsManager.getInstance().launchEntity(
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
        Block block = e.getHitBlock();
        MobCreator.createAllays(block.getLocation().add(e.getHitBlockFace().getDirection()), block.getLocation(), attributes);
        e.getEntity().remove(); // Remove entity now PDC transferred
    }

}
