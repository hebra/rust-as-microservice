package com.github.hebra.ram;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RamAppConfig {

    @Bean
    public Connection connect() {

        var dotenv = Dotenv.load();

        var uri = dotenv.get("DATABASE_URL");
        if (uri == null || uri.isBlank()) {
            throw new RuntimeException("Environment variable DATABASE_URL not set.");
        }

        try {
            String url = "jdbc:".concat(uri);
            log.info("Connecting to database at {}", uri);
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.error("Failed to connect to database at {}: {}", uri, e.getMessage());
            throw new RuntimeException("Database connection setup failed.");
        }
    }
}
