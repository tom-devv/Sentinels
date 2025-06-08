package dev.tom.sentinels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.tom.sentinels.data.DamagingAttributes;
import dev.tom.sentinels.data.SentinelDataWrapper;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProjectileCommand {

    private ProjectileCommand() {
    }

    private static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("projectile")
                .then(Commands.literal("arrow")
                        .executes(ctx -> {
                            if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                            ItemStack arrowItem = ItemStack.of(Material.SPECTRAL_ARROW);
                            DamagingAttributes attr = new DamagingAttributes(0.1, false, 0.5);
                            ItemMeta meta = SentinelDataWrapper.getInstance().savePDC(arrowItem.getItemMeta(), attr);
                            arrowItem.setItemMeta(meta);
                            player.getInventory().addItem(arrowItem);
                            SentinelDataWrapper.getInstance().loadPDC(arrowItem.getItemMeta(), DamagingAttributes.class).ifPresent(attributes ->  {
                                System.out.println(attributes.getDamage() + " " + attributes.hasGravity());
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }

    public static LiteralCommandNode<CommandSourceStack> command() {
        return builder().build();
    }

}
