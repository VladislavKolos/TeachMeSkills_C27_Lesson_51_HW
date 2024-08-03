package org.example.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Utility class for configuring and managing the "HikariCP" connection pool
 * This class allows you to create and configure a data source to connect to a database.
 */
@Component
public class HikariCPDataSource {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource dataSource;

    static {
        config.setJdbcUrl("jdbc:postgresql://localhost:5051/TMS_C27");
        config.setUsername("postgres");
        config.setPassword("postgresql");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setMaximumPoolSize(10);

        dataSource = new HikariDataSource(config);
    }

    public HikariCPDataSource() {
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
