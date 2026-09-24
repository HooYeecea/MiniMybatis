package com.minimybatis.executor;

import com.minimybatis.mapping.BoundSql;
import com.minimybatis.mapping.MappedStatement;
import com.minimybatis.reflection.ParamResolver;
import com.minimybatis.result.DefaultResultSetHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 最简单的执行器：每次操作共用一个 Connection。
 */
public class SimpleExecutor implements Executor {

    private final DataSource dataSource;
    private final boolean autoCommit;
    private Connection connection;

    public SimpleExecutor(DataSource dataSource, boolean autoCommit) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter) throws SQLException {
        BoundSql boundSql = ms.getBoundSql();
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(boundSql.getSql())) {
            bindParameters(ps, boundSql, parameter);
            try (ResultSet rs = ps.executeQuery()) {
                return DefaultResultSetHandler.handle(rs, ms.getResultType());
            }
        }
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        BoundSql boundSql = ms.getBoundSql();
        Connection conn = getConnection();
        try (PreparedStatement ps = conn.prepareStatement(boundSql.getSql())) {
            bindParameters(ps, boundSql, parameter);
            return ps.executeUpdate();
        }
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            } finally {
                Connection c = connection;
                connection = null;
                c.close();
            }
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = dataSource.getConnection();
            connection.setAutoCommit(autoCommit);
        }
        return connection;
    }

    private static void bindParameters(PreparedStatement ps, BoundSql boundSql, Object parameter)
            throws SQLException {
        List<String> names = boundSql.getParameterNames();
        if (names.isEmpty()) {
            return;
        }
        for (int i = 0; i < names.size(); i++) {
            Object value = ParamResolver.getValue(parameter, names.get(i));
            ps.setObject(i + 1, value);
        }
    }
}
