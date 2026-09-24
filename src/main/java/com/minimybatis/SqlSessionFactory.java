package com.minimybatis;

/**
 * 根据 Configuration 打开 SqlSession。
 */
public interface SqlSessionFactory {

    SqlSession openSession();

    SqlSession openSession(boolean autoCommit);

    Configuration getConfiguration();
}
