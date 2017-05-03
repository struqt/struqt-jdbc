package com.struqt.jdbc;

import java.sql.*;

/**
 * Created by wangkang on 9/27/16
 */
public class DoInsert extends DoSql<DoInsert> {

    static DoInsert Get() {
        return new DoInsert();
    }

    private DoInsert() {
    }

    private boolean fetchKey = false;
    private long result = -1;
    private int returnCode = -1;

    @Override
    protected void execute() throws SQLException {
        this.returnCode = -1;
        this.result = -1;
        PreparedStatement stmt = null;
        ResultSet keys = null;
        Connection conn = getConn();
        try {
            stmt = conn.prepareStatement(getSql(), Statement.RETURN_GENERATED_KEYS);
            fillStatement(stmt, getParams().toArray());
            this.returnCode = stmt.executeUpdate();
            if (fetchKey) {
                keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    this.result = keys.getLong(1);
                }
            }
            if (getTask() == null) {
                log.info("{} => code={},result={}", stmt, returnCode, result);
            } else {
                log.info("{} - {} => code={},result={}", getTask(), stmt, returnCode, result);
            }
            close(keys);
            close(stmt);
        } finally {
            closeQuietly(keys);
            closeQuietly(stmt);
        }
    }

    @Override
    public DoInsert reset() {
        fetchKey = false;
        result = -1;
        returnCode = -1;
        return super.reset();
    }


    public DoInsert setFetchKey(boolean fetchKey) {
        this.fetchKey = fetchKey;
        return this;
    }

    public long getResult() throws SQLException {
        if (!isStarted()) {
            start();
        }
        return result;
    }

    public int getReturnCode() throws SQLException {
        if (!isStarted()) {
            start();
        }
        return returnCode;
    }

    public boolean assertReturnCode(int expect) throws SQLException {
        if (expect == getReturnCode()) {
            return true;
        } else {
            throw new SQLException("Expect return code:" + expect + ", but " + getReturnCode());
        }
    }

}
