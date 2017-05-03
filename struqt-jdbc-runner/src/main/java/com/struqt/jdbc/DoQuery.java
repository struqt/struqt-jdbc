package com.struqt.jdbc;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wangkang on 9/27/16
 */
public class DoQuery<T> extends DoSql<DoQuery<T>> {

    static <T> DoQuery<T> Get(Class<T> C) {
        return new DoQuery<>();
    }

    static public class List<T> extends DoQuery<java.util.List<T>> {
    }

    private DoQuery() {
    }

    private ResultSetHandler<T> handler;
    private T result;

    public DoQuery<T> setHandler(ResultSetHandler<T> handler) {
        this.handler = handler;
        return this;
    }

    public T fetch(Connection conn) throws SQLException {
        return setConnection(conn).getResult();
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
    public void start() throws SQLException {
        if (this.handler == null) {
            throw new SQLException("Null ResultSetHandler");
        }
        super.start();
    }


}
