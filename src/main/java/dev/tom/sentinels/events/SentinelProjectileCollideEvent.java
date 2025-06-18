package dev.tom.sentinels.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class SentinelProjectileCollideEvent extends EntityEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Block hitBlock;
    private final BlockFace hitBlockFace;


    public SentinelProjectileCollideEvent(Entity projectile, Block hitBlock, BlockFace hitBlockFace) {
        super(projectile);
        this.hitBlock = hitBlock;
        this.hitBlockFace = hitBlockFace;
    }

    public BlockFace getHitBlockFace() {
        return hitBlockFace;
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
