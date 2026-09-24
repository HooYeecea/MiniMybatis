package com.minimybatis.executor;

import com.minimybatis.mapping.MappedStatement;

import java.sql.SQLException;
import java.util.List;

/**
 * 真正执行 JDBC 的执行器。
 */
public interface Executor {

    <E> List<E> query(MappedStatement ms, Object parameter) throws SQLException;

    int update(MappedStatement ms, Object parameter) throws SQLException;

    void commit() throws SQLException;

    void close() throws SQLException;
}
