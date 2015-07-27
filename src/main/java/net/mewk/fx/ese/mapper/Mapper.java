package net.mewk.fx.ese.mapper;

public interface Mapper<K, T> {
    T map(K object) throws Exception;
}
