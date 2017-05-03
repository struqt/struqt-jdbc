package com.struqt.jdbc;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by wangkang on 5/3/17
 */
public interface Query {

    DataSource getDataSource();

    String getTag();

    static <T> T scalar
        (TransactionTask task, Connection conn, final String sql, final Class<T> C)
        throws SQLException {
        return scalar(task, conn, sql, C, null);
    }

    static <T> T scalar
        (TransactionTask task, Connection conn, final String sql, final Class<T> C, final List<Object> params)
        throws SQLException {
        DoQuery<T> q = DoQuery.Get(C).setConnection(conn)
            .setTask(task).setSql(sql).setHandler(new ScalarHandler<>());
        if (params != null) {
            for (Object param : params) {
                q.addParam(param);
            }
        }
        return q.getResult();
    }

}
