package com.struqt.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wangkang on 9/27/16
 */
@SuppressWarnings("unchecked")
public abstract class DoSql<T extends DoSql<T>> {

    protected abstract void execute() throws SQLException;

    private Connection conn;
    private String sql;
    private List<Object> params;
    private boolean started;
    private JdbcTask task;

    DoSql() {
        params = new ArrayList<>();
    }

    public void start() throws SQLException {
        if (conn == null) {
            throw new SQLException("Null connection");
        }
        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }
        try {
            started = true;
            this.execute();
        } catch (SQLException e) {
            log.info(sql);
            throw e;
        } catch (Exception e) {
            log.info(sql);
            throw new SQLException(e);
        }
    }

    public T reset() {
        this.sql = null;
        this.conn = null;
        this.params.clear();
        this.started = false;
        return (T) this;
    }

    public T setConnection(Connection conn) {
        this.conn = conn;
        return (T) this;
    }

    public T setSql(String sql) {
        this.sql = sql;
        return (T) this;
    }

    public Integer paramCount() {
        return params.size();
    }

    public T addParam(Object o) {
        params.add(o);
        return (T) this;
    }

    public T addParams(Object[] arr) {
        Collections.addAll(params, arr);
        return (T) this;
    }

    protected boolean isStarted() {
        return started;
    }

    protected Connection getConn() {
        return conn;
    }

    protected String getSql() {
        return sql;
    }

    protected List<Object> getParams() {
        return params;
    }

    public T setTask(JdbcTask task) {
        this.task = task;
        return (T) this;
    }

    public JdbcTask getTask() {
        return task;
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    static protected final Logger log = LoggerFactory.getLogger(DoSql.class);

    static public void close(ResultSet o) throws SQLException {
        if (o != null && !o.isClosed()) {
            o.close();
        }
    }

    static public void close(Statement o) throws SQLException {
        if (o != null && !o.isClosed()) {
            o.close();
        }
    }

    static public void closeQuietly(Statement o) {
        try {
            close(o);
        } catch (SQLException e) {
            // quiet
        }
    }

    static public void closeQuietly(ResultSet o) {
        try {
            close(o);
        } catch (SQLException e) {
            // quiet
        }
    }

    static private AtomicBoolean pmdKnownBroken = new AtomicBoolean(false);

    static public void fillStatement(PreparedStatement stmt, Object[] params)
        throws SQLException {

        // check the parameter count, if we can
        ParameterMetaData pmd = null;
        if (!pmdKnownBroken.get()) {
            pmd = stmt.getParameterMetaData();
            int stmtCount = pmd.getParameterCount();
            int paramsCount = params == null ? 0 : params.length;
            if (stmtCount != paramsCount) {
                throw new SQLException("Wrong number of parameters: expected "
                    + stmtCount + ", was given " + paramsCount);
            }
        }

        // nothing to do here
        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type. Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                int sqlType = Types.VARCHAR;
                if (!pmdKnownBroken.get() && pmd != null) {
                    try {
                        /*
                         * It's not possible for pmdKnownBroken to change from
                         * true to false, (once true, always true) so pmd cannot
                         * be null here.
                         */
                        sqlType = pmd.getParameterType(i + 1);
                    } catch (SQLException e) {
                        pmdKnownBroken.compareAndSet(false, true);
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }

}
