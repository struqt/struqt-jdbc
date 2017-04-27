package com.struqt.jdbc;

import org.jooq.DSLContext;
import org.jooq.QueryPart;

/**
 * Created by wangkang on 4/21/17
 */
public interface QueryProvider {

    QueryPart provide(DSLContext dsl);

}
