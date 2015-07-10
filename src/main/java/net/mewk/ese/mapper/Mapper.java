package net.mewk.ese.mapper;

public interface Mapper<K, T> {
    T map(K object);
}
