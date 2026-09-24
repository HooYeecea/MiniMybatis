package com.minimybatis.reflection;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 从参数对象按属性名取值（Map / JavaBean / 单值）。
 */
public final class ParamResolver {

    private ParamResolver() {
    }

    public static Object getValue(Object parameter, String name) {
        if (parameter == null) {
            return null;
        }
        if (parameter instanceof Map<?, ?> map) {
            return map.get(name);
        }
        if (isSimpleValue(parameter)) {
            return parameter;
        }
        try {
            Field field = findField(parameter.getClass(), name);
            field.setAccessible(true);
            return field.get(parameter);
        } catch (Exception e) {
            throw new RuntimeException(
                    "无法从参数读取属性 '" + name + "': " + parameter.getClass().getName(), e);
        }
    }

    private static boolean isSimpleValue(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Enum<?>
                || value instanceof java.util.Date
                || value.getClass().isPrimitive();
    }

    private static Field findField(Class<?> type, String name) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
