package dev.tom.sentinels.regions;

import java.util.Set;

public interface Manager<T> {

    Set<T> getAll();
    void add(T obj);
    void remove(T obj);

}
