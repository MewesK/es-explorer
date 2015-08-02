package net.mewk.fx.esexplorer.mapper;

public interface Mapper<K, T> {
    T map(K object) throws Exception;
}
