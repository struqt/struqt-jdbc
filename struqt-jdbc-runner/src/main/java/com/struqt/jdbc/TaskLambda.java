package com.struqt.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by wangkang on 5/3/17
 */
public interface TaskLambda<T> {

    T invoke(Connection conn, JdbcTask task) throws SQLException;

}
