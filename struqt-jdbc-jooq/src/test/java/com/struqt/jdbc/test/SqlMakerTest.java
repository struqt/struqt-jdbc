package com.struqt.jdbc.test;

import com.struqt.jdbc.QueryCollection;
import com.struqt.jdbc.SqlMaker;
import org.jooq.Log;
import org.jooq.SQLDialect;
import org.jooq.impl.SQLDataType;
import org.jooq.tools.JooqLogger;
import org.junit.FixMethodOrder;
import org.junit.Test;

import java.util.Map;

import static org.jooq.impl.DSL.constraint;
import static org.jooq.impl.DSL.createTable;
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
                data.selectCount().limit(1).offset(1))
            .add("create-table-example", data ->
                createTable("jooq_example")
                    .column("int32", SQLDataType.INTEGER.nullable(false).defaultValue(0))
                    .column("int16", SQLDataType.SMALLINT.nullable(false).defaultValue((short) 0))
                    .column("string", SQLDataType.VARCHAR(50).nullable(false).defaultValue(""))
                    .constraints(
                        constraint().primaryKey("int32"),
                        constraint("UK_001").unique("int16")
                    )
            );
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
            .generate(SQLDialect.MYSQL);
        assertEquals(2, map.size());
        map.forEach((k, v) ->
            System.err.println(k + ": " + v));
    }

}