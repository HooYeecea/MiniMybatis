package com.minimybatis.binding;

import com.minimybatis.SqlSession;
import com.minimybatis.mapping.MappedStatement;
import com.minimybatis.mapping.SqlCommandType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * Mapper 接口动态代理：方法名拼成 statement id，路由到 SqlSession。
 */
public class MapperProxy<T> implements InvocationHandler {

    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        String statementId = mapperInterface.getName() + "." + method.getName();
        MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(statementId);
        Object parameter = args == null || args.length == 0 ? null : args[0];
        if (args != null && args.length > 1) {
            throw new RuntimeException("第一期 Mapper 方法只支持 0 或 1 个参数: " + statementId);
        }

        SqlCommandType type = ms.getSqlCommandType();
        return switch (type) {
            case SELECT -> executeSelect(method, statementId, parameter);
            case INSERT -> sqlSession.insert(statementId, parameter);
            case UPDATE -> sqlSession.update(statementId, parameter);
            case DELETE -> sqlSession.delete(statementId, parameter);
        };
    }

    private Object executeSelect(Method method, String statementId, Object parameter) {
        Class<?> returnType = method.getReturnType();
        if (List.class.isAssignableFrom(returnType) || Collection.class.isAssignableFrom(returnType)) {
            return sqlSession.selectList(statementId, parameter);
        }
        return sqlSession.selectOne(statementId, parameter);
    }
}
