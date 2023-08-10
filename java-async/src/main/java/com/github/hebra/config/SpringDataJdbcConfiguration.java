package com.github.hebra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.sql.Connection;
import java.sql.SQLException;

//@Configuration
public class SpringDataJdbcConfiguration extends AbstractJdbcConfiguration {

    @Override
    public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
        return operations.getJdbcOperations().execute((ConnectionCallback<Dialect>)
                connection -> isSQLite(connection) ? SQLiteDialect.INSTANCE : super.jdbcDialect(operations));
    }

    private boolean isSQLite(Connection connection) throws SQLException {
        return connection.getMetaData().getDatabaseProductName().toUpperCase().contains("SQLITE");
    }

}
