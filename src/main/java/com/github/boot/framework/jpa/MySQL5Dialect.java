package com.github.boot.framework.jpa;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.util.JdbcExceptionHelper;
import org.hibernate.type.StandardBasicTypes;

import java.sql.SQLException;
import java.sql.Types;

/**
 * MySQL方言
 * @author cjh
 * @date 2017/6/22
 */
public class MySQL5Dialect extends MySQLDialect {

    @Override
    protected void registerVarcharTypes() {
        registerColumnType( Types.VARCHAR, "longtext" );
        registerColumnType( Types.VARCHAR, 65535, "varchar($l)" );
        registerColumnType( Types.LONGVARCHAR, "longtext" );
        registerHibernateType( Types.BIGINT, StandardBasicTypes.LONG.getName() );
    }

    @Override
    public boolean supportsColumnCheck() {
        return false;
    }

    @Override
    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
        return EXTRACTER;
    }

    private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
        @Override
        protected String doExtractConstraintName(SQLException e) throws NumberFormatException {
            final int sqlState = Integer.valueOf( JdbcExceptionHelper.extractSqlState( e ) ).intValue();
            switch ( sqlState ) {
                case 23000:
                    return extractUsingTemplate( " for key '", "'", e.getMessage() );
                default:
                    return null;
            }
        }
    };
}
