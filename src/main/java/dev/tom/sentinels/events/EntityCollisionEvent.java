package dev.tom.sentinels.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class EntityCollisionEvent extends EntityEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Block hitBlock;


    public EntityCollisionEvent(Entity flare, Block hitBlock) {
        super(flare);
        this.hitBlock = hitBlock;
    }

    public Block getHitBlock() {
        return hitBlock;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
