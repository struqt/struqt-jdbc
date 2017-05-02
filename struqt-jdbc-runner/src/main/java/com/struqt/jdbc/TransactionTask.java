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
public abstract class TransactionTask<T> {

    private final DataSource dataSource;
    private final String tag;

    public DataSource getDataSource() {
        return dataSource;
    }

    public TransactionTask() {
        this(null, null);
    }

    public TransactionTask(String tag) {
        this(null, tag);
    }

    public TransactionTask(DataSource dataSource) {
        this(dataSource, null);
    }

    public TransactionTask(DataSource dataSource, String tag) {
        this.dataSource = dataSource;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public abstract T run(Connection conn) throws SQLException;

    protected <R> DoQuery<R> queryScalar(Connection conn, Class<R> C) {
        return DoQuery.Get(C).setTask(this).setConnection(conn)
            .setHandler(new ScalarHandler<R>());
    }

    protected <R> DoQuery<List<R>> queryScalars(Connection conn, Class<R> C) {
        return new DoQuery.List<R>().setTask(this).setConnection(conn)
            .setHandler(new ColumnListHandler<R>());
    }

    protected <R> DoQuery<R> queryBean(Connection conn, Class<R> C) {
        return DoQuery.Get(C).setTask(this).setConnection(conn)
            .setHandler(new BeanHandler<>(C));
    }

    protected <R> DoQuery<List<R>> queryBeans(Connection conn, Class<R> C) {
        return new DoQuery.List<R>().setTask(this).setConnection(conn)
            .setHandler(new BeanListHandler<>(C));
    }

    protected DoQuery<List<Map<String, Object>>> queryMaps(Connection conn) {
        return new DoQuery.List<Map<String, Object>>()
            .setTask(this).setConnection(conn)
            .setHandler(new MapListHandler());
    }

    protected DoQuery<List<Object[]>> queryArrays(Connection conn) {
        return new DoQuery.List<Object[]>()
            .setTask(this).setConnection(conn)
            .setHandler(new ArrayListHandler());
    }

    protected DoInsert dataInsert(Connection conn) {
        return DoInsert.Get().setTask(this).setConnection(conn);
    }

    protected DoUpdate dataUpdate(Connection conn) {
        return DoUpdate.Get().setTask(this).setConnection(conn);
    }

    protected DoBatch dataBatch(Connection conn) {
        return DoBatch.Get().setTask(this).setConnection(conn);
    }

    @Override
    public String toString() {
        return "TransactionTask{" +
            "in='" + super.toString() + '\'' +
            ",tag='" + tag + '\'' +
            '}';
    }
}