package com.minimybatis.mapping;

/**
 * 一条 MappedStatement：对应 mapper 中的一个 select/insert/update/delete。
 */
public class MappedStatement {

    private final String id;
    private final SqlCommandType sqlCommandType;
    private final String sql;
    private final Class<?> resultType;

    public MappedStatement(String id, SqlCommandType sqlCommandType, String sql, Class<?> resultType) {
        this.id = id;
        this.sqlCommandType = sqlCommandType;
        this.sql = sql;
        this.resultType = resultType;
    }

    public String getId() {
        return id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public String getSql() {
        return sql;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public BoundSql getBoundSql() {
        return SqlParser.parse(sql);
    }
}
