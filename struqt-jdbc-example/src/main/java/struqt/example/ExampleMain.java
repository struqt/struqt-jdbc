package struqt.example;

import com.struqt.jdbc.QueryCollection;
import com.struqt.jdbc.SqlMaker;
import org.jooq.Log;
import org.jooq.SQLDialect;
import org.jooq.tools.JooqLogger;

import java.util.Map;

import static org.jooq.impl.DSL.inline;
import static struqt.example.tables.SchemaVersion.SCHEMA_VERSION;

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
        return new QueryCollection("example")
            .add("schema-version-select", data ->
                data.select(
                    SCHEMA_VERSION.VERSION,
                    SCHEMA_VERSION.APPLIED_ON,
                    SCHEMA_VERSION.DURATION)
                    .from(SCHEMA_VERSION)
                    .where(SCHEMA_VERSION.DURATION.ge(inline(0)))
                    .and(SCHEMA_VERSION.DURATION.lt(10))
                    .orderBy(SCHEMA_VERSION.VERSION)
            );
    }

}
