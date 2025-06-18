package dev.tom.sentinels.projectiles.items;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.projectiles.ItemSupplier;
import dev.tom.sentinels.utils.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.format.TextColor.color;


public class ItemCreator<T extends Record & Serializable & ItemSupplier> {

    private final T supplier;

    public ItemCreator(T supplier) {
        this.supplier = supplier;
    }

    public ItemStack create() {
        ItemStack itemStack = ItemStack.of(supplier.getMaterial());
        itemStack.editMeta(meta -> {
            meta.lore(getLore());
        });

        SentinelDataWrapper.getInstance().savePDC(itemStack, supplier);
        return itemStack;
    }


    private List<Component> getLore() {
        List<Component> lore = new ArrayList<>();
        RecordComponent[] recordComponents = supplier.getClass().getRecordComponents();
        for (RecordComponent recordComponent : recordComponents) {
            String name = recordComponent.getName();
            Object value;

            try {
                value = recordComponent.getAccessor().invoke(supplier);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.err.println("Error accessing ItemSupplier: " + supplier.getClass().getName() + " field: " + name);
                lore.add(
                        Component.text()
                        .content("Failed to resolve lore")
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.BOLD, true)
                        .build()
                );
                continue;
            }
            Component loreComponent = parseValue(name, value);
            lore.add(loreComponent);
        }
        return lore;
    }

    private Component parseValue(String rawName, Object value){
        String fieldName = TextUtil.upperCaseFirstLetter(rawName).strip();
        TextComponent.Builder builder = Component.text()
                .content(fieldName).color(color(0x3768db))
                .append(Component.text(": ").color(NamedTextColor.GRAY));
        if (value instanceof Boolean boolValue) {
            builder.append(Component.text(boolValue ? "Yes" : "No").color(boolValue ? NamedTextColor.GREEN : NamedTextColor.RED));
        } else if (value == null) {
            builder.append(Component.text("N/A").color(NamedTextColor.DARK_GRAY));
        } else {
            builder.append(Component.text(value.toString()).color(NamedTextColor.WHITE));
        }

        String unit = switch (rawName) {
            case "radius" -> "m";
            case "velocity" -> "m/t";
            case "healing" -> "hp/t";
            default -> null;
        };

        if(unit != null){
            builder.append(Component.text(unit).color(NamedTextColor.WHITE));
        }

        return builder.build();
    }




}
