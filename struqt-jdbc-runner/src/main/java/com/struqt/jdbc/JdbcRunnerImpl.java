package com.struqt.jdbc;

import com.struqt.jdbc.config.DataSourceConf;
import com.struqt.jdbc.config.JdbcRunnerConf;
import com.struqt.jdbc.config.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by wangkang on 5/2/17
 */
public class JdbcRunnerImpl implements JdbcRunner {

    static private final Logger log = LoggerFactory.getLogger(JdbcRunnerImpl.class);
    static private final Pattern multiSpacePattern = Pattern.compile("[\\s]+");

    private final JdbcRunnerConf config;
    private final Map<String, DataSource> mapDataSource = new HashMap<>();
    private final Map<String, String> mapDataSourceDialect = new HashMap<>();
    private final Map<String, Statement> mapSql = new HashMap<>(128);

    public JdbcRunnerImpl(JdbcRunnerConf config) {
        this.config = config;
        initSqlStatements(config.getMapStatement());
    }

    public Long queryCount(String key, Object... params) {
        return queryScalar(key, Long.class, params);
    }

    public <T> T queryScalar(String key, final Class<T> C, final Object... params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryScalar(C, task, conn, getSql(key), params)));
    }

    public <T> T queryScalar(String key, final Class<T> C, final List<Object> params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryScalar(C, task, conn, getSql(key), params)));
    }

    public <T> List<T> queryScalars(String key, final Object... params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryScalars(task, conn, getSql(key), params)));
    }

    public <T> List<T> queryScalars(String key, final List<Object> params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryScalars(task, conn, getSql(key), params)));
    }

    public <T> T queryBean(String key, final Class<T> C, final Object... params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryBean(C, task, conn, getSql(key), params)));
    }

    public <T> T queryBean(String key, final Class<T> C, final List<Object> params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryBean(C, task, conn, getSql(key), params)));
    }

    public <T> List<T> queryBeans(String key, final Class<T> C, final Object... params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryBeans(C, task, conn, getSql(key), params)));
    }

    public <T> List<T> queryBeans(String key, final Class<T> C, final List<Object> params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryBeans(C, task, conn, getSql(key), params)));
    }

    public List<Map<String, Object>> queryMaps(String key, final Object... params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryMaps(task, conn, getSql(key), params)));
    }

    public List<Map<String, Object>> queryMaps(String key, final List<Object> params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryMaps(task, conn, getSql(key), params)));
    }

    public List<Object[]> queryArrays(String key, final Object... params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryArrays(task, conn, getSql(key), params)));
    }

    public List<Object[]> queryArrays(String key, final List<Object> params) {
        return transactionAuto(key, null, getDataSource(getSqlConfig(key)), ((conn, task) ->
            JdbcTask.queryArrays(task, conn, getSql(key), params)));
    }


    public <T> T transaction(TaskLambda<T> run) {
        return transaction("task?", null, null, run);
    }

    public <T> T transaction(String tag, TaskLambda<T> run) {
        return transaction(tag, null, null, run);
    }

    public <T> T transaction(String tag, T deft, DataSource ds, TaskLambda<T> run) {
        if (ds == null) {
            ds = getDataSource();
        }
        TransactionTaskDefault<T> task = new TransactionTaskDefault<>(ds, tag, getSql(tag), run);
        return Transaction.execute(task.getDataSource(), task, deft);
    }

    public <T> T transactionAuto(TaskLambda<T> run) {
        return transactionAuto("task?", null, null, run);
    }

    public <T> T transactionAuto(String tag, TaskLambda<T> run) {
        return transactionAuto(tag, null, null, run);
    }

    public <T> T transactionAuto(String tag, T deft, DataSource ds, TaskLambda<T> run) {
        if (ds == null) {
            ds = getDataSource();
        }
        TransactionTaskDefault<T> task = new TransactionTaskDefault<>(ds, tag, getSql(tag), run);
        return Transaction.executeAuto(task.getDataSource(), task, deft);
    }

    public <T> T transaction(T deft, TransactionTask<T> task) {
        if (task.getDataSource() != null) {
            return Transaction.execute(task.getDataSource(), task, deft);
        } else {
            return Transaction.execute(getDataSource(), task, deft);
        }
    }

    public <T> T transactionAuto(T deft, TransactionTask<T> task) {
        if (task.getDataSource() != null) {
            return Transaction.executeAuto(task.getDataSource(), task, deft);
        } else {
            return Transaction.executeAuto(getDataSource(), task, deft);
        }
    }


    public DataSource getDataSource() {
        return getDataSource(config.getDefaultDataSource(),
            getSqlDialect(config.getDefaultDataSource()));
    }


    public String getSqlDialect(String key) {
        String dialect = mapDataSourceDialect.get(key);
        if (dialect != null && dialect.length() > 0) {
            return dialect;
        }
        DataSourceConf conf = config.getMapDataSource().get(key);
        if (conf == null) {
            return null;
        }
        return conf.getDialect();
    }

    public DataSource getDataSource(String key, String dialect) {
        DataSource ds = mapDataSource.get(key);
        if (ds == null) {
            ds = makeDataSource(key);
        }
        String expect = mapDataSourceDialect.get(key);
        if (expect == null) {
            return null;
        }
        if (!expect.equals(dialect)) {
            log.error("SQL dialect mismatch: '{}' is '{}', SQL is '{}'", key, expect, dialect);
            return null;
        }
        return ds;
    }

    public String getSql(String key) {
        Statement s = getSqlConfig(key);
        if (s == null) {
            return null;
        }
        return s.getSql();
    }

    private DataSource getDataSource(Statement s) {
        if (s == null) {
            log.error("Statement arg is null");
            return null;
        }
        String key = s.getDataSource();
        if (key == null || key.length() <= 0) {
            key = config.getDefaultDataSource();
        }
        if (key == null || key.length() <= 0) {
            log.error("key of Statement arg is null");
            return null;
        } else {
            DataSource ds = getDataSource(key, s.getDialect());
            if (ds == null) {
                log.error("No {} datasource with key '{}'", s.getDialect(), key);
            }
            return ds;
        }
    }

    private Statement getSqlConfig(String key) {
        return mapSql.get(key);
    }

    private void initSqlStatements(LinkedHashMap<String, Statement> statements) {
        if (statements == null) {
            return;
        }
        for (Map.Entry<String, Statement> s : statements.entrySet()) {
            if (s.getKey().length() <= 0) {
                continue;
            }
            if (s.getValue().getSql() == null) {
                continue;
            }
            String sql = s.getValue().getSql().trim();
            if (sql.length() <= 0) {
                continue;
            }
            sql = multiSpacePattern.matcher(sql).replaceAll(" ");
            if (sql.length() <= 0) {
                continue;
            }
            s.getValue().setSql(sql);
            if (mapSql.containsKey(s.getKey())) {
                log.warn("Repeated sql statement key: {}", s.getKey());
            }
            String key = "";
            if (config.getModuleName() != null && config.getModuleName().length() > 0) {
                key += config.getModuleName().trim();
            }
            if (key.length() > 0) {
                key += '/';
            }
            key += s.getKey();
            mapSql.put(key, s.getValue());
        }
    }

    private DataSource makeDataSource(String key) {
        if (mapDataSource.containsKey(key)) {
            return mapDataSource.get(key);
        }
        if (config == null) {
            log.error("JdbcRunnerImpl.config is null");
            return null;
        }
        DataSourceConf conf = null;
        if (config.getMapDataSource() != null) {
            conf = config.getMapDataSource().get(key);
        }
        if (conf == null) {
            log.error("No data source config with key '{}'", key);
            return null;
        }
        try {
            Class<?> Cls = Class.forName(conf.getProvider());
            Object o = Cls.newInstance();
            if (o instanceof DataSourceProvider) {
                DataSourceProvider p = ((DataSourceProvider) o);
                p.setConfig(conf);
                DataSource ds = p.getDataSource();
                mapDataSource.put(key, ds);
                mapDataSourceDialect.put(key, conf.getDialect());
                return ds;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
