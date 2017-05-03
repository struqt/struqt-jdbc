package com.struqt.jdbc;

import com.struqt.jdbc.config.DataSourceConf;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * Created by wangkang on 9/18/16
 */
public class DataSourceHikari extends DataSourceProvider {

    private final Logger log = LoggerFactory.getLogger(DataSourceHikari.class);

    @Override
    public DataSource getDataSource() {
        DataSourceConf conf = getConfig();
        if (conf == null) {
            return null;
        }
        //log.info(conf.toStringJSON());
        Properties props = new Properties();
        for (Map.Entry<String, String> attr : conf.getProperties().entrySet()) {
            props.put(attr.getKey(), attr.getValue());
        }
        final String jdbcUrl = super.getJdbcUrl();
        props.put("jdbcUrl", jdbcUrl == null ? "null" : jdbcUrl);
        props.put("username", conf.getUsername());
        props.put("password", conf.getPassword());
        log.info(jdbcUrl);
        HikariDataSource ds = new HikariDataSource(new HikariConfig(props));
        ds.setPoolName(conf.getKey());
        for (Map.Entry<String, String> attr : conf.getAttributes().entrySet()) {
            ds.addDataSourceProperty(attr.getKey(), attr.getValue());
        }
        return ds;
    }

}
