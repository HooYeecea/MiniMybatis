package com.minimybatis;

import com.minimybatis.binding.MapperRegistry;
import com.minimybatis.mapping.MappedStatement;
import com.minimybatis.parsing.XmlMapperParser;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局配置：DataSource、MappedStatement、Mapper 注册表。
 */
public class Configuration {

    private DataSource dataSource;
    private final Map<String, MappedStatement> mappedStatements = new HashMap<>();
    private final MapperRegistry mapperRegistry = new MapperRegistry(this);
    private final Map<String, Class<?>> typeAliases = new HashMap<>();

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addMappedStatement(MappedStatement mappedStatement) {
        if (mappedStatements.containsKey(mappedStatement.getId())) {
            throw new RuntimeException("MappedStatement 已存在: " + mappedStatement.getId());
        }
        mappedStatements.put(mappedStatement.getId(), mappedStatement);
    }

    public MappedStatement getMappedStatement(String id) {
        MappedStatement ms = mappedStatements.get(id);
        if (ms == null) {
            throw new RuntimeException("找不到 MappedStatement: " + id);
        }
        return ms;
    }

    public boolean hasMappedStatement(String id) {
        return mappedStatements.containsKey(id);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public <T> boolean hasMapper(Class<T> type) {
        return mapperRegistry.hasMapper(type);
    }

    public java.util.Set<Class<?>> getMapperTypes() {
        return mapperRegistry.getMappers();
    }

    public void addTypeAlias(String alias, Class<?> type) {
        typeAliases.put(alias.toLowerCase(), type);
    }

    public Class<?> resolveType(String typeName) {
        if (typeName == null || typeName.isBlank()) {
            return null;
        }
        Class<?> alias = typeAliases.get(typeName.toLowerCase());
        if (alias != null) {
            return alias;
        }
        try {
            return Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到类型: " + typeName, e);
        }
    }

    /**
     * 解析 classpath 上的 mapper XML，并注册 namespace 对应的 Mapper 接口（若存在）。
     */
    public void addMapperXml(String classpathLocation) {
        XmlMapperParser.parse(this, classpathLocation);
    }
}
