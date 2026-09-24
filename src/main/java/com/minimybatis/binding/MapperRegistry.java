package com.minimybatis.binding;

import com.minimybatis.Configuration;
import com.minimybatis.SqlSession;

import java.util.HashMap;
import java.util.Map;

/**
 * 已注册的 Mapper 接口 → 代理工厂。
 */
public class MapperRegistry {

    private final Configuration configuration;
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    public <T> void addMapper(Class<T> type) {
        if (!type.isInterface()) {
            throw new RuntimeException("Mapper 必须是接口: " + type.getName());
        }
        if (knownMappers.containsKey(type)) {
            return;
        }
        knownMappers.put(type, new MapperProxyFactory<>(type));
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public java.util.Set<Class<?>> getMappers() {
        return java.util.Collections.unmodifiableSet(knownMappers.keySet());
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<T> factory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (factory == null) {
            throw new RuntimeException("未注册 Mapper: " + type.getName());
        }
        return factory.newInstance(sqlSession);
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
