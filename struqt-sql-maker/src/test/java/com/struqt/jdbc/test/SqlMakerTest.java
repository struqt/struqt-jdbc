package com.struqt.jdbc.test;

import com.struqt.jdbc.QueryCollection;
import com.struqt.jdbc.SqlMaker;
import org.jooq.Log;
import org.jooq.SQLDialect;
import org.jooq.tools.JooqLogger;
import org.junit.FixMethodOrder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by wangkang on 4/21/17
 */
@FixMethodOrder
public class SqlMakerTest {

    static {
        JooqLogger.globalThreshold(Log.Level.ERROR);
    }

    @Test
    public void testSqlMaker() {
        QueryCollection c1 = new QueryCollection("test.a")
            .add("simple-select", data ->
                data.select().limit(1).offset(1))
            .add("simple-count", data ->
                data.selectCount().limit(1).offset(1));
        QueryCollection c2 = new QueryCollection("test.b")
            .add("simple-select", data ->
                data.select().limit(1).offset(1))
            .add("simple-count", data ->
                data.selectCount().limit(1).offset(1));
        Map<String, String> map = new SqlMaker()
            .add(c1)
            .add(c2)
            .generate(SQLDialect.MYSQL);
        map.forEach((k, v) ->
            System.err.println(k + ": " + v));
    }

    @Test
    public void testQueryCollection() {
        Map<String, String> map = new QueryCollection("test")
            .add("simple-select", data ->
                data.select().limit(1).offset(1))
            .add("simple-count", data ->
                data.selectCount().limit(1).offset(1))
            .generate(SQLDialect.MYSQL, true);
        assertEquals(2, map.size());
        map.forEach((k, v) ->
            System.err.println(k + ": " + v));
    }

}