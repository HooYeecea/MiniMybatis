package com.minimybatis.mapping;

import java.util.Collections;
import java.util.List;

/**
 * 一条已解析 SQL：包含 ? 占位符与参数名顺序。
 */
public class BoundSql {

    private final String sql;
    private final List<String> parameterNames;

    public BoundSql(String sql, List<String> parameterNames) {
        this.sql = sql;
        this.parameterNames = parameterNames == null
                ? List.of()
                : Collections.unmodifiableList(parameterNames);
    }

    public String getSql() {
        return sql;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }
}
