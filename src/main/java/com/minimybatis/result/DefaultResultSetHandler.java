package com.minimybatis.result;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 把 ResultSet 映射成 resultType（Map / JavaBean / 简单类型）。
 */
public final class DefaultResultSetHandler {

    private DefaultResultSetHandler() {
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> handle(ResultSet rs, Class<?> resultType) throws SQLException {
        List<E> list = new ArrayList<>();
        if (resultType == null || resultType == void.class || resultType == Void.class) {
            return list;
        }
        if (Map.class.isAssignableFrom(resultType)) {
            while (rs.next()) {
                list.add((E) toMap(rs));
            }
            return list;
        }
        if (isSimpleType(resultType)) {
            while (rs.next()) {
                list.add((E) getColumnValue(rs, 1, resultType));
            }
            return list;
        }
        while (rs.next()) {
            list.add((E) toBean(rs, resultType));
        }
        return list;
    }

    private static Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int count = meta.getColumnCount();
        Map<String, Object> row = new HashMap<>();
        for (int i = 1; i <= count; i++) {
            String label = meta.getColumnLabel(i);
            row.put(label, rs.getObject(i));
        }
        return row;
    }

    private static Object toBean(ResultSet rs, Class<?> resultType) throws SQLException {
        try {
            Object bean = resultType.getDeclaredConstructor().newInstance();
            ResultSetMetaData meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (int i = 1; i <= count; i++) {
                String label = meta.getColumnLabel(i);
                Field field = findFieldIgnoreCase(resultType, label);
                if (field == null) {
                    continue;
                }
                field.setAccessible(true);
                Object value = getColumnValue(rs, i, field.getType());
                field.set(bean, value);
            }
            return bean;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("结果映射失败: " + resultType.getName(), e);
        }
    }

    private static Field findFieldIgnoreCase(Class<?> type, String column) {
        String normalized = column.replace("_", "");
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.getName().equalsIgnoreCase(column)
                        || field.getName().equalsIgnoreCase(normalized)
                        || field.getName().replace("_", "").equalsIgnoreCase(normalized)) {
                    return field;
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private static Object getColumnValue(ResultSet rs, int index, Class<?> targetType) throws SQLException {
        Object raw = rs.getObject(index);
        if (raw == null || targetType.isInstance(raw)) {
            return raw;
        }
        if (targetType == int.class || targetType == Integer.class) {
            return rs.getInt(index);
        }
        if (targetType == long.class || targetType == Long.class) {
            return rs.getLong(index);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return rs.getBoolean(index);
        }
        if (targetType == double.class || targetType == Double.class) {
            return rs.getDouble(index);
        }
        if (targetType == float.class || targetType == Float.class) {
            return rs.getFloat(index);
        }
        if (targetType == String.class) {
            return rs.getString(index);
        }
        if (targetType == BigDecimal.class) {
            return rs.getBigDecimal(index);
        }
        return raw;
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Character.class
                || type == java.util.Date.class
                || type == BigDecimal.class;
    }
}
