package com.struqt.jdbc;

import org.apache.commons.dbutils.handlers.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by wangkang on 9/24/16
 */
@SuppressWarnings("ALL")
public abstract class TransactionTask<T> implements JdbcTask {

    public abstract T run(Connection conn) throws SQLException;

    private final String tag;
    private final String sql;
    private final DataSource dataSource;

    public TransactionTask() {
        this(null, null, null);
    }

    public TransactionTask(DataSource dataSource) {
        this(dataSource, null, null);
    }

    public TransactionTask(String tag) {
        this(null, tag, null);
    }

    public TransactionTask(DataSource dataSource, String tag, String sql) {
        this.dataSource = dataSource;
        this.tag = tag;
        this.sql = sql;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getTag() {
        return tag;
    }


    /* ********************************************************************** */


    public DoInsert dataInsert() {
        return DoInsert.Get().setTask(this).setSql(sql);
    }

    public DoUpdate dataUpdate() {
        return DoUpdate.Get().setTask(this).setSql(sql);
    }

    public DoBatch dataBatch() {
        return DoBatch.Get().setTask(this).setSql(sql);
    }

    public <R> DoQuery<R> queryScalar(Class<R> C) {
        return DoQuery.Get(C).setTask(this)
            .setSql(sql).setHandler(new ScalarHandler<>());
    }

    public <R> DoQuery<List<R>> queryScalars(Class<R> C) {
        return new DoQuery.List<R>().setTask(this)
            .setSql(sql).setHandler(new ColumnListHandler<>());
    }

    public <R> DoQuery<R> queryBean(Class<R> C) {
        return DoQuery.Get(C).setTask(this)
            .setSql(sql).setHandler(new BeanHandler<>(C));
    }

    public <R> DoQuery<List<R>> queryBeans(Class<R> C) {
        return new DoQuery.List<R>().setTask(this)
            .setSql(sql).setHandler(new BeanListHandler<>(C));
    }

    public DoQuery<List<Map<String, Object>>> queryMaps() {
        return new DoQuery.List<Map<String, Object>>().setTask(this)
            .setSql(sql).setHandler(new MapListHandler());
    }

    public DoQuery<List<Object[]>> queryArrays() {
        return new DoQuery.List<Object[]>().setTask(this)
            .setSql(sql).setHandler(new ArrayListHandler());
    }


    /* ********************************************************************** */


    public DoInsert dataInsert(Connection conn) {
        return dataInsert().setConnection(conn);
    }

    public DoUpdate dataUpdate(Connection conn) {
        return dataUpdate().setConnection(conn);
    }

    public DoBatch dataBatch(Connection conn) {
        return dataBatch().setConnection(conn);
    }

    public <R> DoQuery<R> queryScalar(Connection conn, Class<R> C) {
        return queryScalar(C).setConnection(conn);
    }

    public <R> DoQuery<List<R>> queryScalars(Connection conn, Class<R> C) {
        return queryScalars(C).setConnection(conn);
    }

    public <R> DoQuery<R> queryBean(Connection conn, Class<R> C) {
        return queryBean(C).setConnection(conn);
    }

    public <R> DoQuery<List<R>> queryBeans(Connection conn, Class<R> C) {
        return queryBeans(C).setConnection(conn);
    }

    public DoQuery<List<Map<String, Object>>> queryMaps(Connection conn) {
        return queryMaps().setConnection(conn);
    }

    public DoQuery<List<Object[]>> queryArrays(Connection conn) {
        return queryArrays().setConnection(conn);
    }


    /* ********************************************************************** */


    @Override
    public String toString() {
        return "TransactionTask{"
            + "in='" + super.toString() + '\''
            + ",tag='" + tag + '\'' + '}';
    }

}