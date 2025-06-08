package dev.tom.sentinels.regions;

import java.util.Objects;

/**
 * Represents a definable flag that can be set on a Region.
 * This provides type safety for flag values.
 *
 * @param <T> The type of the value associated with this flag.
 */
public class RegionFlag<T> {
    private final String key;
    private final T defaultValue;
    private final Class<T> type;

    private RegionFlag(String key, T defaultValue, Class<T> type) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public Class<T> getType() {
        return type;
    }

    // Static factory methods for common flag types
    public static RegionFlag<Boolean> booleanFlag(String key, boolean defaultValue) {
        return new RegionFlag<>(key, defaultValue, Boolean.class);
    }

    public static RegionFlag<String> stringFlag(String key, String defaultValue) {
        return new RegionFlag<>(key, defaultValue, String.class);
    }

    public static RegionFlag<Integer> intFlag(String key, int defaultValue) {
        return new RegionFlag<>(key, defaultValue, Integer.class);
    }

    /**
     * Creates a new RegionFlag for an enum type.
     *
     * @param key The unique key for this flag.
     * @param defaultValue The default enum value for this flag.
     * @param enumType The Class object of the enum
     * @param <E> The enum type.
     * @return A new RegionFlag instance for the specified enum.
     */
    public static <E extends Enum<E>> RegionFlag<E> enumFlag(String key, E defaultValue, Class<E> enumType) {
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException("Type must be an enum for enumFlag. Provided: " + enumType.getName());
        }
        return new RegionFlag<>(key, defaultValue, enumType);
    }

    // Flags
    public static final RegionFlag<RegionState> STATE = enumFlag("state", RegionState.NEUTRAL, RegionState.class);
    public static final RegionFlag<Boolean> PVP_ENABLED = booleanFlag("pvp-enabled", true);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionFlag<?> that = (RegionFlag<?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}