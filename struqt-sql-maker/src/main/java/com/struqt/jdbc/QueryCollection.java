package com.struqt.jdbc;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.jooq.impl.DSL.using;

/**
 * Created by wangkang on 4/21/17
 */
public class QueryCollection {

    //private static Pattern multiSpacePattern = Pattern.compile("[\\s]+");

    private final Map<String, QueryProvider> providers;
    private final String name;

    public QueryCollection() {
        this("sql", new LinkedHashMap<>());
    }

    public QueryCollection(String name) {
        this(name, new LinkedHashMap<>());
    }

    public QueryCollection(String name, Map<String, QueryProvider> providers) {
        this.name = name == null ? "" : name;
        this.providers = providers;
    }

    public QueryCollection add(String key, QueryProvider dsl) {
        providers.put(key, dsl);
        return this;
    }

    public Map<String, String> generate(SQLDialect dialect) {
        Map<String, String> map = new LinkedHashMap<>(providers.size());
        DSLContext dsl = using(dialect);
        providers.forEach((k, v) -> {
            String sql = dsl.renderInlined(v.provide(dsl));
            //String key = dialect.getName();
            String key = "";
            if (name != null && name.length() > 0) {
                key += '/';
                key += name;
            }
            key += '/';
            key += k;
            map.put(key, sql);
        });
        return map;
    }

    public String getName() {
        return name;
    }
}
