package com.github.hebra.config;

import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

public class SQLiteDialectResolver implements DialectResolver.JdbcDialectProvider {

    private static final String DATABASE_NAME = "sqlite";

    @Override
    public Optional<Dialect> getDialect(JdbcOperations operations) {
        return Optional.ofNullable(operations.execute((ConnectionCallback<Dialect>) this::getDialect));
    }

    private Dialect getDialect(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ROOT);
        if (name.contains(DATABASE_NAME)) {
            return SQLiteDialect.INSTANCE;
        }
        return null;
    }
}

