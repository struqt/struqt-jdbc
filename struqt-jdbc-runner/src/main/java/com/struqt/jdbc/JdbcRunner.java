package com.struqt.jdbc;

import com.struqt.jdbc.config.Statement;

import javax.sql.DataSource;

/**
 * Created by wangkang on 5/3/17
 */
public interface JdbcRunner {

    <T> T startTransaction(T deft, TransactionTask<T> task);

    <T> T startTransactionAuto(T deft, TransactionTask<T> task);

    String getSql(String key);

    DataSource getDataSource();

    DataSource getDataSource(Statement s);

    DataSource getDataSource(String key, String dialect);




    Long queryCount(String key);

    <T> T queryScalar(String key, final Class<T> C, final Object... params);

}
