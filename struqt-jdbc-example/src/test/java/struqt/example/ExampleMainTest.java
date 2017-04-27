package struqt.example;

import org.jooq.SQLDialect;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by wangkang on 4/27/17
 */
public class ExampleMainTest {

    @BeforeClass
    public static void init() {
        ExampleMain.init(SQLDialect.MYSQL)
            .forEach((k, v) -> System.err.println(k + ": " + v));
    }

    @Test
    public void test() {
    }

}
