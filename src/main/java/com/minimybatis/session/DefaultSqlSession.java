package com.minimybatis.session;

import com.minimybatis.Configuration;
import com.minimybatis.SqlSession;
import com.minimybatis.executor.Executor;
import com.minimybatis.mapping.MappedStatement;
import com.minimybatis.mapping.SqlCommandType;

import java.sql.SQLException;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    public DefaultSqlSession(Configuration configuration, Executor executor) {
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        List<T> list = selectList(statement, parameter);
        if (list.isEmpty()) {
            return null;
        }
        if (list.size() > 1) {
            throw new RuntimeException("期望 1 条结果，实际 " + list.size() + ": " + statement);
        }
        return list.get(0);
    }

    @Override
    public <T> List<T> selectList(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.query(ms, parameter);
        } catch (SQLException e) {
            throw new RuntimeException("查询失败: " + statement, e);
        }
    }

    @Override
    public int insert(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public int update(String statement, Object parameter) {
        MappedStatement ms = configuration.getMappedStatement(statement);
        try {
            return executor.update(ms, parameter);
        } catch (SQLException e) {
            throw new RuntimeException("更新失败: " + statement, e);
        }
    }

    @Override
    public int delete(String statement, Object parameter) {
        return update(statement, parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void commit() {
        try {
            executor.commit();
        } catch (SQLException e) {
            throw new RuntimeException("commit 失败", e);
        }
    }

    @Override
    public void close() {
        try {
            executor.close();
        } catch (SQLException e) {
            throw new RuntimeException("关闭 SqlSession 失败", e);
        }
    }

    /** 供调试：确认 statement 类型。 */
    public SqlCommandType getCommandType(String statement) {
        return configuration.getMappedStatement(statement).getSqlCommandType();
    }
}
