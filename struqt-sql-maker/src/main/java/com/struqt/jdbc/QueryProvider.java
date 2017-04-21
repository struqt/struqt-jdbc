package com.struqt.jdbc;

import org.jooq.DSLContext;
import org.jooq.Query;

/**
 * Created by wangkang on 4/21/17
 */
public interface QueryProvider {

    Query provide(DSLContext dsl);

}
