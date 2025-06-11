package dev.tom.sentinels.data;

import org.bukkit.entity.Entity;

import java.io.Serializable;

public record PDCTransferResult<T extends Serializable, E extends Entity>(T data, E entity){};