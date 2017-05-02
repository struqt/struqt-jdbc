package com.struqt.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by wangkang on 9/24/16
 */
public class Transaction {

    static private final Logger log = LoggerFactory.getLogger(Transaction.class);

    static public <T> T execute(DataSource dataSrc, TransactionTask<T> task, T deftResult) {
        if (dataSrc == null) {
            log.error("Transaction won't start, no data source, task: {}", task.toString());
            return deftResult;
        }
        T result = null;
        Connection conn = null;
        int auto = -1;
        try {
            conn = dataSrc.getConnection();
            auto = conn.getAutoCommit() ? 0 : 1;
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            T result0 = task.run(conn);
            if (result0 != null) {
                result = result0;
            }
            conn.commit();
            log.info("Transaction has been committed, task: {}", task.toString());
            close(conn);
        } catch (SQLException e) {
            log.error("Transaction will rollback, task failed: {}", task.toString(), e);
            rollback(conn);
        } finally {
            try {
                if (auto >= 0 && conn != null) {
                    conn.setAutoCommit(auto == 0);
                }
            } catch (SQLException e) {
                // quiet
            } finally {
                closeQuietly(conn);
            }
        }
        return result == null ? deftResult : result;
    }

    static public <T> T executeAuto(DataSource dataSrc, TransactionTask<T> task, T deftResult) {
        if (dataSrc == null) {
            log.error("Transaction won't start, no data source, task: {}", task.toString());
            return deftResult;
        }
        T result = null;
        Connection conn = null;
        int auto = -1;
        try {
            conn = dataSrc.getConnection();
            auto = conn.getAutoCommit() ? 0 : 1;
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            T result0 = task.run(conn);
            if (result0 != null) {
                result = result0;
            }
            log.info("Transaction has been auto committed, task: {}", task.toString());
            close(conn);
        } catch (SQLException e) {
            log.error("Transaction won't rollback, task failed: {}", task.toString(), e);
        } finally {
            try {
                if (auto >= 0 && conn != null) {
                    conn.setAutoCommit(auto == 0);
                }
            } catch (SQLException ignored) {
            } finally {
                closeQuietly(conn);
            }
        }
        return result == null ? deftResult : result;
    }


    static public void rollback(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (SQLException e) {
            log.error(null, e);
        } finally {
            closeQuietly(conn);
        }
    }

    static public void close(Connection o) throws SQLException {
        if (o != null && !o.isClosed()) {
            o.close();
        }
    }

    static public void closeQuietly(Connection conn) {
        try {
            close(conn);
        } catch (SQLException e) {
            // quiet
        }
    }

}
