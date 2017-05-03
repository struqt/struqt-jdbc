package com.struqt.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by wangkang on 5/3/17
 */
class TransactionTaskDefault<T> extends TransactionTask<T> {

    private final TaskLambda<T> lambda;

    TransactionTaskDefault(DataSource dataSource, String tag, String sql, TaskLambda<T> lambda) {
        super(dataSource, tag, sql);
        this.lambda = lambda;
    }

    @Override
    public T run(Connection conn) throws SQLException {
        if (lambda != null) {
            return lambda.invoke(conn, this);
        }
        return null;
    }

}
