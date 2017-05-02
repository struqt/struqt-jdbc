package com.struqt.jdbc;

import org.jooq.DSLContext;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderKeywordStyle;
import org.jooq.conf.Settings;
import org.jooq.conf.StatementType;

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
        this(null, new LinkedHashMap<>());
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
        DSLContext dsl = using(dialect, makeSettings());
        providers.forEach((k, v) -> {
            Query q = v.provide(dsl);
            String sql = dsl.render(q);
            //String key = dialect.getName();
            String key = "";
            if (name != null && name.length() > 0) {
                key += '/';
                key += name;
            }
            key += '/';
            key += k;
            key += "@" + paramCount(q);
            map.put(key, sql);
        });
        return map;
    }

    private int paramCount(Query q) {
        int count = 0;
        for (Map.Entry<String, Param<?>> p : q.getParams().entrySet()) {
            if (!p.getValue().isInline()) {
                count++;
            }
        }
        return count;
    }

    private Settings makeSettings() {
        Settings settings = new Settings();
        settings.setStatementType(StatementType.PREPARED_STATEMENT);
        settings.setRenderSchema(false);
        settings.setRenderKeywordStyle(RenderKeywordStyle.LOWER);
        settings.setRenderFormatted(true);
        return settings;
    }

    public String getName() {
        return name;
    }
}
