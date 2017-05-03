package com.struqt.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangkang on 10/21/16
 */
public class DoBatch extends DoSql<DoBatch> {

    static DoBatch Get() {
        return new DoBatch();
    }

    private DoBatch() {
    }

    private List<Object[]> params = new ArrayList<>(16);

    public DoBatch addParam(Object... args) {
        params.add(args);
        return this;
    }

    @Override
    public Integer paramCount() {
        return params.size();
    }

    public DoBatch addParam(Object o) {
        if (o instanceof Object[]) {
            params.add((Object[]) o);
        }
        return this;
    }

    public DoBatch addParams(Object[] arr) {
        params.add(arr);
        return this;
    }

    @Override
    protected void execute() throws SQLException {
        if (params.size() <= 0) {
            throw new SQLException("No parameters.");
        }
        final Connection conn = getConn();
        final String sql = getSql();
        if (conn == null) {
            throw new SQLException("Null connection");
        }
        if (sql == null) {
            throw new SQLException("Null SQL statement");
        }
        PreparedStatement stmt = null;
        int[] rows;
        try {
            stmt = conn.prepareStatement(sql);
            for (Object[] param : params) {
                fillStatement(stmt, param);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();
            if (getTask() == null) {
                log.info("{} => {}", stmt, rows);
            } else {
                log.info("{} - {} => {}", getTask(), stmt, rows);
            }
        } finally {
            closeQuietly(stmt);
        }
    }

}
