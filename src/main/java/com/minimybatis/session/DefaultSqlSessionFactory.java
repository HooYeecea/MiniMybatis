package com.minimybatis.session;

import com.minimybatis.Configuration;
import com.minimybatis.SqlSession;
import com.minimybatis.SqlSessionFactory;
import com.minimybatis.executor.SimpleExecutor;

public class DefaultSqlSessionFactory implements SqlSessionFactory {

    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return openSession(false);
    }

    @Override
    public SqlSession openSession(boolean autoCommit) {
        if (configuration.getDataSource() == null) {
            throw new RuntimeException("Configuration 未设置 DataSource");
        }
        return new DefaultSqlSession(
                configuration,
                new SimpleExecutor(configuration.getDataSource(), autoCommit));
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
}
