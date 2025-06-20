package dev.tom.sentinels.commands;



import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.tom.sentinels.launchable.impl.flares.FlareAttributes;
import dev.tom.sentinels.launchable.items.ItemCreator;
import dev.tom.sentinels.launchable.impl.shells.ShellAttributes;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProjectileCommand {

    private ProjectileCommand() {
    }

    private static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return Commands.literal("projectile")
                .then(Commands.literal("shell")
                        .then(Commands.argument("damage", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("radius", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("gravity", BoolArgumentType.bool())
                        .then(Commands.argument("speed", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("knockback", DoubleArgumentType.doubleArg(0))
                            .executes(ctx -> {
                                if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                double damage = DoubleArgumentType.getDouble(ctx, "damage");
                                boolean gravity = BoolArgumentType.getBool(ctx, "gravity");
                                double radius = DoubleArgumentType.getDouble(ctx, "radius");
                                double speed = DoubleArgumentType.getDouble(ctx, "speed");
                                double knockback = DoubleArgumentType.getDouble(ctx, "knockback");
                                ShellAttributes attr = new ShellAttributes(player.getUniqueId(), damage, speed, gravity, radius, knockback);
                                ItemStack arrowItem = new ItemCreator<>(attr).create();
                                player.getInventory().addItem(arrowItem);
                                return Command.SINGLE_SUCCESS;
                            })
                ))))))
                .then(Commands.literal("flare")
                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                        .then(Commands.argument("health", DoubleArgumentType.doubleArg(0.1))
                        .then(Commands.argument("healing", DoubleArgumentType.doubleArg(0.01))
                        .then(Commands.argument("gravity", BoolArgumentType.bool())
                        .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                        .then(Commands.argument("velocity", DoubleArgumentType.doubleArg(0.1))
                            .executes(ctx -> {
                                if(!(ctx.getSource().getSender() instanceof Player player)) return 0;
                                int count = IntegerArgumentType.getInteger(ctx, "count");
                                double health = DoubleArgumentType.getDouble(ctx, "health");
                                double healing = DoubleArgumentType.getDouble(ctx, "healing");
                                boolean gravity = BoolArgumentType.getBool(ctx, "gravity");
                                int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                double velocity = DoubleArgumentType.getDouble(ctx, "velocity");
                                FlareAttributes attr = new FlareAttributes(player.getUniqueId(), gravity, healing, health, count, radius, velocity);
                                ItemStack flareItem = new ItemCreator<>(attr).create();
                                player.getInventory().addItem(flareItem);
                                return Command.SINGLE_SUCCESS;
                            })
                        ))))))
                );

    }

    public static LiteralCommandNode<CommandSourceStack> command() {
        return builder().build();
    }

}
