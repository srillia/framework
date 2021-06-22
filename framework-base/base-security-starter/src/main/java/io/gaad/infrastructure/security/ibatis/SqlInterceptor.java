package io.gaad.infrastructure.security.ibatis;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;


import java.sql.Connection;


@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class SqlInterceptor extends AbstractSqlParserHandler implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        this.sqlParser(metaObject);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        //如果为SELECT or DELETE类型。则不处理，直接执行
        if (SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType()) || SqlCommandType.DELETE.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        //获取最终执行的sql
        String originalSql = boundSql.getSql();
        if (SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType())) {
            metaObject.setValue("delegate.boundSql.sql", SqlBuildUtil.buildSql(originalSql, SqlCommandType.UPDATE.name()));
            return invocation.proceed();
        }
        if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
            metaObject.setValue("delegate.boundSql.sql", SqlBuildUtil.buildSql(originalSql, SqlCommandType.INSERT.name()));
            return invocation.proceed();
        }
        return invocation.proceed();
    }


}
