package com.struqt.jdbc;

import org.apache.commons.dbutils.handlers.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by wangkang on 5/3/17
 */
@SuppressWarnings("unused")
public interface JdbcTask {

    DataSource getDataSource();

    String getTag();


    DoBatch dataBatch(Connection conn);

    DoInsert dataInsert(Connection conn);

    DoUpdate dataUpdate(Connection conn);

    <R> DoQuery<R> queryScalar(Connection conn, Class<R> C);

    <R> DoQuery<List<R>> queryScalars(Connection conn, Class<R> C);

    <R> DoQuery<R> queryBean(Connection conn, Class<R> C);

    <R> DoQuery<List<R>> queryBeans(Connection conn, Class<R> C);

    DoQuery<List<Map<String, Object>>> queryMaps(Connection conn);

    DoQuery<List<Object[]>> queryArrays(Connection conn);


    DoBatch dataBatch();

    DoInsert dataInsert();

    DoUpdate dataUpdate();

    <R> DoQuery<R> queryScalar(Class<R> C);

    <R> DoQuery<List<R>> queryScalars(Class<R> C);

    <R> DoQuery<R> queryBean(Class<R> C);

    <R> DoQuery<List<R>> queryBeans(Class<R> C);

    DoQuery<List<Map<String, Object>>> queryMaps();

    DoQuery<List<Object[]>> queryArrays();


    //TODO dataBatch();

    static long dataInsert
        (boolean fetchKey, JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoInsert Do = DoInsert.Get()
            .setTask(task).setConnection(conn).setSql(sql).setFetchKey(fetchKey);
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        if (fetchKey) {
            return Do.getResult();
        } else {
            return (long) Do.getReturnCode();
        }
    }

    static long dataUpdate
        (JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoUpdate Do = DoUpdate.Get()
            .setTask(task).setConnection(conn).setSql(sql);
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getReturnCode();

    }

    static long dataUpdate
        (Integer expectCode, JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoUpdate Do = DoUpdate.Get().setConnection(conn).setTask(task)
            .setSql(sql).setExpectCode(expectCode);
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getReturnCode();

    }

    static <T> T queryScalar
        (Class<T> C, JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoQuery<T> Do = DoQuery.Get(C)
            .setTask(task).setConnection(conn)
            .setSql(sql).setHandler(new ScalarHandler<>());
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getResult();
    }

    static <T> List<T> queryScalars
        (JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoQuery<List<T>> Do = new DoQuery.List<T>()
            .setTask(task).setConnection(conn)
            .setSql(sql).setHandler(new ColumnListHandler<>());
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getResult();
    }

    static <T> T queryBean
        (Class<T> C, JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoQuery<T> Do = DoQuery.Get(C)
            .setTask(task).setConnection(conn)
            .setSql(sql).setHandler(new BeanHandler<>(C));
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getResult();
    }

    static <T> List<T> queryBeans
        (Class<T> C, JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoQuery<List<T>> Do = new DoQuery.List<T>()
            .setTask(task).setConnection(conn)
            .setSql(sql).setHandler(new BeanListHandler<>(C));
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getResult();
    }

    static List<Map<String, Object>> queryMaps
        (JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoQuery<List<Map<String, Object>>> Do = new DoQuery.List<Map<String, Object>>()
            .setTask(task).setConnection(conn)
            .setSql(sql).setHandler(new MapListHandler());
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getResult();
    }

    static List<Object[]> queryArrays
        (JdbcTask task, Connection conn, String sql, List<Object> params)
        throws SQLException {
        DoQuery<List<Object[]>> Do = new DoQuery.List<Object[]>()
            .setTask(task).setConnection(conn)
            .setSql(sql).setHandler(new ArrayListHandler());
        Optional.of(params).ifPresent(iter -> iter.forEach(Do::addParam));
        return Do.getResult();
    }


    static long dataInsert
        (boolean fetchKey, JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return dataInsert(fetchKey, task, conn, sql, list);
    }

    static long dataUpdate
        (JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return dataUpdate(task, conn, sql, list);
    }

    static long dataUpdate
        (Integer expectCode, JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return dataUpdate(expectCode, task, conn, sql, list);
    }

    static <T> T queryScalar
        (Class<T> C, JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return queryScalar(C, task, conn, sql, list);
    }

    static <T> List<T> queryScalars
        (JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return queryScalars(task, conn, sql, list);
    }

    static <T> T queryBean
        (Class<T> C, JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return queryBean(C, task, conn, sql, list);
    }

    static <T> List<T> queryBeans
        (Class<T> C, JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return queryBeans(C, task, conn, sql, list);
    }

    static List<Map<String, Object>> queryMaps
        (JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return queryMaps(task, conn, sql, list);
    }

    static List<Object[]> queryArrays
        (JdbcTask task, Connection conn, String sql, Object... params)
        throws SQLException {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return queryArrays(task, conn, sql, list);
    }

}
