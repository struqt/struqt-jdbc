package com.struqt.jdbc;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangkang on 9/27/16
 */
public class DoQuery<T> extends DoSql<DoQuery<T>> {

    @SuppressWarnings("unused")
    static public <T> DoQuery<T> Get(Class<T> C) {
        return new DoQuery<>();
    }

    static public class List<T> extends DoQuery<java.util.List<T>> {
    }

    protected DoQuery() {
    }

    ResultSetHandler<T> handler;
    T result;

    public DoQuery<T> setHandler(ResultSetHandler<T> handler) {
        this.handler = handler;
        return this;
    }

    public T getResult() throws SQLException {
        if (!isStarted()) {
            start();
        }
        return result;
    }

    @Override
    protected void execute() throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rset = null;
        try {
            stmt = getConn().prepareStatement(getSql());
            fillStatement(stmt, getParams().toArray());
            rset = stmt.executeQuery();
            result = this.handler.handle(rset);
            if (getTask() == null) {
                log.info("{} => {}", stmt, result);
            } else {
                log.info("{} - {} => {}", getTask(), stmt, result);
            }
            close(rset);
            close(stmt);
        } finally {
            closeQuietly(rset);
            closeQuietly(stmt);
        }
    }

    @Override
    public DoQuery<T> start() throws SQLException {
        if (this.handler == null) {
            throw new SQLException("Null ResultSetHandler");
        }
        super.start();
        return this;
    }


}
