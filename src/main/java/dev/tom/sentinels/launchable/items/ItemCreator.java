package dev.tom.sentinels.launchable.items;

import dev.tom.sentinels.data.SentinelDataWrapper;
import dev.tom.sentinels.launchable.ItemSupplier;
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

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public class ItemCreator<T extends Serializable & ItemSupplier> {

    private final T supplier;

    public ItemCreator(T supplier) {
        this.supplier = supplier;
    }

    public ItemStack create() {
        ItemStack itemStack = ItemStack.of(supplier.material());
        itemStack.editMeta(meta -> {
            meta.displayName(supplier.nameComponent());
            meta.lore(getLore());
        });
        SentinelDataWrapper.getInstance().savePDC(itemStack, supplier);
        return itemStack;
    }

    private List<Component> getLore() {
        List<Component> lore = new ArrayList<>(supplier.prefixLoreComponent());
        lore.addAll(parsedComponents());
        return lore;
    }

    private List<Component> parsedComponents(){
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
                        text()
                            .content("Failed to resolve lore")
                            .color(NamedTextColor.RED)
                            .decoration(TextDecoration.BOLD, true)
                            .build()
                );
                continue;
            }
            Component loreComponent = parseComponent(recordComponent, value);
            if(loreComponent != null) {
                lore.add(loreComponent);
            }
        }
        return lore;
    }


    private Component parseComponent(RecordComponent component, Object value){
        FieldInfo info = component.getAnnotation(FieldInfo.class);
        if(info != null && info.ignore()) {
            return null;
        }
        String fieldName = getPrettyFieldName(component, info);
        TextComponent.Builder builder = text()
                .decoration(TextDecoration.ITALIC, false)
                .content(fieldName).color(color(0x3768db))
                .append(text(": ").color(NamedTextColor.GRAY));
        if (value instanceof Boolean boolValue) {
            builder.append(text(boolValue ? "Yes" : "No"));
        } else if (value == null) {
            builder.append(text("N/A").color(NamedTextColor.DARK_GRAY));
        } else {
            builder.append(text(value.toString()).color(NamedTextColor.WHITE));
        }

        String unit;
        if(info != null && !(unit = info.unit()).equalsIgnoreCase("")) {
            builder.append(text(unit).color(NamedTextColor.WHITE));
        }

        return builder.build();
    }


    /**
     * Get a pretty field name from a component either from its
     * annotation or by its field name
     * @param component
     * @param info
     * @return pretty field name
     */
    private String getPrettyFieldName(RecordComponent component, FieldInfo info){
        String fieldName = TextUtil.prettyFieldName(component.getName());

        // display name set via annotation
        if(info != null && !info.name().equalsIgnoreCase("")) {
            fieldName = info.name();
        }
        return fieldName;
    }



}
