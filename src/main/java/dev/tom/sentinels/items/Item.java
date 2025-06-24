package dev.tom.sentinels.items;

import dev.tom.sentinels.data.SentinelDataWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;

public abstract class Item<T extends Serializable> {

    protected final ItemStack item;
    protected final Class<T> type;
    protected final T attributes;


    public <I extends Serializable & ItemSupplier> Item(@NotNull I supplier, @NotNull Class<T> type){
        this.item = new ItemCreator<>(supplier).create();
        this.type = type;
        this.attributes = setAttributes();
    }

    public Item(@NotNull ItemStack item, @NotNull Class<T> type) {
        this.item = item;
        this.type = type;
        this.attributes = setAttributes();
    }

    private T setAttributes() {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("Item does not have metadata: " + item);
        }

        Optional<T> loaded = SentinelDataWrapper.getInstance().loadPDC(meta, type);
        if (loaded.isEmpty()) {
            throw new RuntimeException("Failed to load item PDC: " + item + " of type " + type);
        }

        return loaded.get();
    }

    public ItemStack getItem() {
        return item;
    }

    public Class<T> getType() {
        return type;
    }

    public T getAttributes() {
        return attributes;
    }

    public boolean isOfType() {
        return SentinelDataWrapper.getInstance().isType(item.getItemMeta(), type);
    }
}