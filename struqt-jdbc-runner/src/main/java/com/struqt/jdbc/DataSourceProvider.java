package com.struqt.jdbc;

import com.struqt.jdbc.config.DataSourceConf;

import javax.sql.DataSource;

/**
 * Created by wangkang on 9/18/16
 */
public abstract class DataSourceProvider {

    private DataSourceConf config;

    public DataSourceConf getConfig() {
        return config;
    }

    public void setConfig(DataSourceConf config) {
        this.config = config;
    }

    public final String getJdbcUrl() {
        if (config == null) {
            return null;
        }
        if (config.getJdbcUrl() == null || config.getJdbcUrl().length() <= 0) {
            return config.getJdbcUrl().trim();
        }
        if (config.getDatabase() == null || config.getDatabase().length() <= 0) {
            return config.getJdbcUrl().trim();
        } else {
            if (config.getJdbcUrl().endsWith("/")) {
                return config.getJdbcUrl().trim() + config.getDatabase().trim();

            } else {
                return config.getJdbcUrl().trim() + "/" + config.getDatabase().trim();
            }
        }
    }

    public abstract DataSource getDataSource();
}
