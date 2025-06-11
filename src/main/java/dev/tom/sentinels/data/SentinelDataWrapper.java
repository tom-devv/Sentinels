package dev.tom.sentinels.data;

import dev.tom.sentinels.Sentinels;
import net.kyori.adventure.key.Namespaced;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.Optional;

public class SentinelDataWrapper {

    private final NamespacedKey key;


    private static SentinelDataWrapper INSTANCE;

    private SentinelDataWrapper() {
        this.key = new NamespacedKey(Sentinels.getInstance(), "sentinels-data");
    }

    public static SentinelDataWrapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SentinelDataWrapper();
        }
        return INSTANCE;
    }

    public <T extends Serializable, P extends PersistentDataHolder> boolean isType(P holder, Class<T> type){
        Optional<T> optionalData = loadPDC(holder, type);
        return optionalData.isPresent();
    }

    public <T extends Serializable> ItemStack savePDC(ItemStack item, T data){
        ItemMeta meta = item.getItemMeta();
        savePDC(meta, data);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Save a serializable class to a PDC
     * @param holder the PDC
     * @param data class data
     * @param <T> Type
     */
    public <T extends Serializable, P extends PersistentDataHolder> P savePDC(P holder, T data)  {
        byte[] bytes;
        try {
            bytes = serializeToByteArray(data);
        } catch (IOException e) {
            throw new RuntimeException("FAILED TO SAVE TO PDC: " + e);
        }
        holder.getPersistentDataContainer().set(key, PersistentDataType.BYTE_ARRAY, bytes);
        return holder;
    }


    public <T extends Serializable> Optional<T> loadPDC(PersistentDataHolder holder, Class<T> type) {
        byte[] data = holder.getPersistentDataContainer().get(key, PersistentDataType.BYTE_ARRAY);
        if(data == null) return Optional.empty();
        try {
            Object deserialized = deserialize(data);
            if(type.isInstance(deserialized)) {
                return Optional.of(type.cast(deserialized));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] serializeToByteArray(Object object) throws IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

    private static Object deserialize(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ois != null) {
                ois.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }
}
