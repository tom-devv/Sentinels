package dev.tom.sentinels.commands;



import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tom.sentinels.projectiles.shells.ShellAttributes;
import dev.tom.sentinels.projectiles.flares.FlareAttributes;
import dev.tom.sentinels.data.SentinelDataWrapper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProjectileCommand {

    private ProjectileCommand() {
    }

    private static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("projectile")
                .then(Commands.literal("arrow")
                        .then(Commands.argument("damage", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("gravity", BoolArgumentType.bool())
                            .executes(ctx -> {
                                if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                ItemStack arrowItem = ItemStack.of(Material.SPECTRAL_ARROW);
                                double damage = DoubleArgumentType.getDouble(ctx, "damage");
                                boolean gravity = BoolArgumentType.getBool(ctx, "gravity");
                                double radius = DoubleArgumentType.getDouble(ctx, "radius");
                                ShellAttributes attr = new ShellAttributes(damage, gravity, radius);
                                SentinelDataWrapper.getInstance().savePDC(arrowItem, attr);
                                player.getInventory().addItem(arrowItem);
                                return Command.SINGLE_SUCCESS;
                            })
                ))))
                .then(Commands.literal("flare")
                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                        .then(Commands.argument("health", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("healing", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("gravity", BoolArgumentType.bool())
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                            .executes(ctx -> {
                                if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                ItemStack flareItem = ItemStack.of(Material.RED_CANDLE);
                                int count = IntegerArgumentType.getInteger(ctx, "count");
                                double health = DoubleArgumentType.getDouble(ctx, "health");
                                double healing = DoubleArgumentType.getDouble(ctx, "healing");
                                boolean gravity = BoolArgumentType.getBool(ctx, "gravity");
                                int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                FlareAttributes attr = new FlareAttributes(player, gravity, healing, health, count, radius);
                                SentinelDataWrapper.getInstance().savePDC(flareItem, attr);
                                player.getInventory().addItem(flareItem);
                                return Command.SINGLE_SUCCESS;
                            })
                        )))))
                );

    }

    public static LiteralCommandNode<CommandSourceStack> command() {
        return builder().build();
    }

}
