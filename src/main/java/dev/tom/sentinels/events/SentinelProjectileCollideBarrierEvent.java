package dev.tom.sentinels.events;

import dev.tom.sentinels.regions.protection.Barrier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SentinelProjectileCollideBarrierEvent extends SentinelProjectileCollideEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull Barrier barrier;

    public SentinelProjectileCollideBarrierEvent(Entity projectile, Block hitBlock, BlockFace hitBlockFace, @NotNull Barrier barrier) {
        super(projectile, hitBlock, hitBlockFace);
        this.barrier = barrier;
    }

    public @NotNull Barrier getBarrier() {
        return barrier;
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
