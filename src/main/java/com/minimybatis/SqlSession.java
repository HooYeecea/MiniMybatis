package com.minimybatis;

import java.util.List;

/**
 * 一次数据库会话：执行 SQL、拿 Mapper 代理、提交/关闭。
 */
public interface SqlSession extends AutoCloseable {

    <T> T selectOne(String statement, Object parameter);

    <T> List<T> selectList(String statement, Object parameter);

    int insert(String statement, Object parameter);

    int update(String statement, Object parameter);

    int delete(String statement, Object parameter);

    <T> T getMapper(Class<T> type);

    Configuration getConfiguration();

    void commit();

    @Override
    void close();
}
