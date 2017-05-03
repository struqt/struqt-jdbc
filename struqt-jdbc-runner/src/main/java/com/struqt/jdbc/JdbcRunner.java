package com.struqt.jdbc;

import com.struqt.jdbc.config.DataSourceConf;
import com.struqt.jdbc.config.JdbcRunnerConf;
import com.struqt.jdbc.config.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by wangkang on 5/2/17
 */
public class JdbcRunner {

    static private final Logger log = LoggerFactory.getLogger(JdbcRunner.class);
    static private final Pattern multiSpacePattern = Pattern.compile("[\\s]+");
    private final Map<String, DataSource> mapDataSource = new HashMap<>();
    private final Map<String, String> mapDataSourceDialect = new HashMap<>();
    private final Map<String, Statement> mapSql = new HashMap<>(128);
    private final JdbcRunnerConf config;

    public JdbcRunner(JdbcRunnerConf config) {
        this.config = config;
        initSqlStatements(config.getMapStatement());
    }

    public Long queryCount(String key) {
        return queryScalar(key, Long.class);
    }

    public <T> T queryScalar(String key, final Class<T> C, final Object... params) {
        List<Object> list = new ArrayList<>(params.length);
        Collections.addAll(list, params);
        return queryScalar(key, C, list);
    }

    public <T> T queryScalar(String key, final Class<T> C, final List<Object> params) {
        Statement s = getSqlConfig(key);
        if (s == null) {
            return null;
        }
        final String sql = s.getSql();
        return startTransactionAuto(null, new TransactionTask<T>(getDataSource(s), key) {
            @Override
            public T run(Connection conn) throws SQLException {
                DoQuery<T> q = queryScalar(conn, C).setSql(sql);
                if (params != null) {
                    params.forEach(q::addParam);
                }
                return q.getResult();
            }
        });
    }

    public <T> T startTransaction(T deft, TransactionTask<T> task) {
        if (task.getDataSource() != null) {
            return Transaction.execute(task.getDataSource(), task, deft);
        } else {
            return Transaction.execute(getDataSource(), task, deft);
        }
    }

    public <T> T startTransactionAuto(T deft, TransactionTask<T> task) {
        if (task.getDataSource() != null) {
            return Transaction.executeAuto(task.getDataSource(), task, deft);
        } else {
            return Transaction.executeAuto(getDataSource(), task, deft);
        }
    }

    public DataSource getDataSource() {
        return getDataSource(config.getDefaultDataSource(), "MySQL");
    }

    public DataSource getDataSource(Statement s) {
        if (s == null) {
            return null;
        }
        String key = s.getDataSource();
        if (key == null || key.length() <= 0) {
            key = config.getDefaultDataSource();
        }
        if (key == null || key.length() <= 0) {
            return null;
        } else {
            return getDataSource(key, s.getDialect());
        }
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
            log.error("JdbcRunner.config is null");
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
