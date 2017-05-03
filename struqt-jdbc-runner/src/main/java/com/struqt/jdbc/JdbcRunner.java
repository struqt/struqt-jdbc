package com.struqt.jdbc;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by wangkang on 5/3/17
 */
@SuppressWarnings("ALL")
public interface JdbcRunner {

    <T> T transaction(T deft, TransactionTask<T> task);

    <T> T transactionAuto(T deft, TransactionTask<T> task);

    <T> T transaction(TaskLambda<T> run);

    <T> T transaction(String tag, TaskLambda<T> run);

    <T> T transaction(String tag, T deft, DataSource ds, TaskLambda<T> run);

    <T> T transactionAuto(TaskLambda<T> run);

    <T> T transactionAuto(String tag, TaskLambda<T> run);

    <T> T transactionAuto(String tag, T deft, DataSource ds, TaskLambda<T> run);


    String getSql(String key);

    String getSqlDialect(String key);

    DataSource getDataSource();

    DataSource getDataSource(String key, String dialect);


    Long queryCount(String key, Object... params);

    <T> T queryScalar(String key, final Class<T> C, final Object... params);

    <T> T queryScalar(String key, final Class<T> C, final List<Object> params);

    <T> List<T> queryScalars(String key, final Object... params);

    <T> List<T> queryScalars(String key, final List<Object> params);

    <T> T queryBean(String key, final Class<T> C, final Object... params);

    <T> T queryBean(String key, final Class<T> C, final List<Object> params);

    <T> List<T> queryBeans(String key, final Class<T> C, final Object... params);

    <T> List<T> queryBeans(String key, final Class<T> C, final List<Object> params);

    List<Map<String, Object>> queryMaps(String key, final Object... params);

    List<Map<String, Object>> queryMaps(String key, final List<Object> params);

    List<Object[]> queryArrays(String key, final Object... params);

    List<Object[]> queryArrays(String key, final List<Object> params);

}
