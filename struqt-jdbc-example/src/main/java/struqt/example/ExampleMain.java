package struqt.example;

import com.struqt.jdbc.QueryCollection;
import com.struqt.jdbc.SqlMaker;
import org.jooq.Field;
import org.jooq.Log;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.JooqLogger;

import java.sql.Timestamp;
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

    static private final SqlMaker sqlMaker = new SqlMaker();

    static public void main(String[] args) {
        init(SQLDialect.MYSQL);
    }

    static public Map<String, String> init(SQLDialect dialect) {
        return sqlMaker
            .add(initExample())
            .generate(dialect);
    }

    static QueryCollection initExample() {
        return new QueryCollection()
            .add("account-select-all", data ->
                data.select()
                    .from(ACCOUNT)
                    .leftJoin(ACCOUNT_SECURITY).onKey()
                    .where(ACCOUNT.ID.eq(0L))
                    .and(ifnull(ACCOUNT_SECURITY.NO_LOGIN, inline(0)).eq(inline(0)))
            );
    }

    public static Field<Long> unixTimestamp(Field<Timestamp> arg) {
        return DSL.field("unix_timestamp({0})", Long.class, arg);
    }

}
