package dev.tom.sentinels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tom.sentinels.items.ItemCreator;
import dev.tom.sentinels.items.rally.Flag;
import dev.tom.sentinels.items.rally.FlagAttributes;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommand {

    private ItemCommand() {
    }

    private static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("sitem")
                .then(Commands.literal("flag")
                        .executes(ctx -> {
                            if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                            ItemStack item = new ItemCreator<>(new FlagAttributes()).create();
                            player.getInventory().addItem(item);
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }


    public static LiteralCommandNode<CommandSourceStack> command() {
        return builder().build();
    }
}

