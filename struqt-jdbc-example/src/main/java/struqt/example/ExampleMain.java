package struqt.example;

import com.struqt.jdbc.*;
import com.struqt.jdbc.config.JdbcRunnerConf;
import invar.lib.data.DataMapper;
import org.jooq.Field;
import org.jooq.Log;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.JooqLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.ifnull;
import static org.jooq.impl.DSL.inline;
import static struqt.example.Tables.ACCOUNT;
import static struqt.example.Tables.ACCOUNT_SECURITY;

/**
 * Created by wangkang on 4/27/17
 */
public class ExampleMain {

    static {
        JooqLogger.globalThreshold(Log.Level.ERROR);
    }

    static private final Logger log = LoggerFactory.getLogger(ExampleMain.class);
    static private final SqlMaker sqlMaker = new SqlMaker();
    static private JdbcRunner jdbc;

    static public void main(String[] args) throws Exception {
        initJdbcRunner();
        init(SQLDialect.MYSQL);

        testQueryScalar();
        testQueryArrays();
    }

    private static void initJdbcRunner() throws Exception {
        jdbc = new JdbcRunnerImpl(jdbcConfig());
    }

    private static void testQueryScalar() {
        final String format = "----- {}";

        Timestamp t1 = jdbc.transaction(null, new TransactionTask<Timestamp>("select-now-datetime") {
            @Override
            public Timestamp run(Connection conn) throws SQLException {
                String sql = jdbc.getSql(getTag());
                return JdbcTask.queryScalar(Timestamp.class, this, conn, sql);
            }
        });
        log.info(format, t1);

        Timestamp t2 = jdbc.transactionAuto("select-now-datetime", (conn, task) ->
            task.queryScalar(Timestamp.class).fetch(conn));
        log.info(format, t2);

        Timestamp t3 = jdbc.queryScalar("select-now-datetime", Timestamp.class);
        log.info(format, t3);

        Timestamp t4 = jdbc.transaction(ExampleMain::selectNow);
        log.info(format, t4);
    }

    private static void testQueryArrays() {
        List<Object[]> a = jdbc.queryArrays("select-database-versions");
        log.info(a.toString());
    }

    private static Timestamp selectNow(Connection conn, JdbcTask task) throws SQLException {
        return task.queryScalar(Timestamp.class)
            .setSql(jdbc.getSql("select-now-datetime"))
            .fetch(conn);
    }

    static Map<String, String> init(SQLDialect dialect) {
        return sqlMaker
            .add(initExample())
            .generate(dialect);
    }

    private static QueryCollection initExample() {
        return new QueryCollection()
            .add("account-select-all", data ->
                data.select()
                    .from(ACCOUNT)
                    .leftJoin(ACCOUNT_SECURITY).onKey()
                    .where(ACCOUNT.ID.eq(0L))
                    .and(ifnull(ACCOUNT_SECURITY.NO_LOGIN, inline(0)).eq(inline(0)))
            );
    }

    private static Field<Long> unixTimestamp(Field<Timestamp> arg) {
        return DSL.field("unix_timestamp({0})", Long.class, arg);
    }

    private static JdbcRunnerConf jdbcConfig() throws Exception {
        final String path = "/invar/data/example.xml";
        JdbcRunnerConf config = JdbcRunnerConf.Create();
        InputStream stream = JdbcRunnerConf.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IOException("No config resource at " + path);
        }
        DataMapper.forXml().map(config, stream);
        return config;
    }

}
