package com.minimybatis.mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 把 #{name} 换成 ?，并记录参数名顺序。不做动态 SQL。
 */
public final class SqlParser {

    private static final Pattern HASH_PARAM = Pattern.compile("#\\{([^}]+)}");

    private SqlParser() {
    }

    public static BoundSql parse(String rawSql) {
        List<String> names = new ArrayList<>();
        Matcher matcher = HASH_PARAM.matcher(rawSql);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            names.add(matcher.group(1).trim());
            matcher.appendReplacement(sb, "?");
        }
        matcher.appendTail(sb);
        return new BoundSql(sb.toString(), names);
    }
}
