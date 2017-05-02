package com.struqt.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by wangkang on 9/27/16
 */
public class DoUpdate extends DoSql<DoUpdate> {

    static public DoUpdate Get() {
        return new DoUpdate();
    }

    private DoUpdate() {
    }

    Integer expectCode = null;
    Integer returnCode;
    boolean logOnChange;

    public DoUpdate setLogOnChange(boolean logOnChange) {
        this.logOnChange = logOnChange;
        return this;
    }

    public Integer getReturnCode() throws SQLException {
        if (!isStarted()) {
            start();
        }
        return returnCode;
    }

    public void setExpectCode(Integer expectCode) {
        this.expectCode = expectCode;
    }

    @Override
    protected void execute() throws SQLException {
        PreparedStatement stmt = null;
        Connection conn = getConn();
        try {
            stmt = conn.prepareStatement(getSql());
            fillStatement(stmt, getParams().toArray());
            returnCode = stmt.executeUpdate();
            boolean enableLog = true;
            if (logOnChange) {
                enableLog = (returnCode > 0);
            }
            if (enableLog) {
                if (getTask() == null) {
                    log.info("{} => {}", stmt, returnCode);
                } else {
                    log.info("{} - {} => {}", getTask(), stmt, returnCode);
                }
            }
            if (expectCode != null && !expectCode.equals(returnCode)) {
                throw new SQLException("Update expect " + expectCode + ", but " + returnCode);
            }
            close(stmt);
        } finally {
            closeQuietly(stmt);
        }
    }

}
