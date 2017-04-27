package com.struqt.jdbc;

import org.jooq.SQLDialect;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wangkang on 4/21/17
 */
public class SqlMaker {

    private final Map<String, QueryCollection> collections;

    public SqlMaker() {
        this(new LinkedHashMap<>());
    }

    public SqlMaker(Map<String, QueryCollection> collections) {
        this.collections = collections;
    }

    public SqlMaker add(QueryCollection collection) {
        collections.put(collection.getName(), collection);
        return this;
    }

    public Map<String, String> generate(SQLDialect dialect) {
        Map<String, String> map = new LinkedHashMap<>();
        collections.forEach((k, v) -> {
            Map<String, String> m = v.generate(dialect);
            map.putAll(m);
        });
        return map;
    }

}