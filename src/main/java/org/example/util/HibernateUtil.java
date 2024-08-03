package org.example.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Grooup;
import org.example.model.RecordBook;
import org.example.model.Student;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Utility class for managing connections to the "PostgreSQL" database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HibernateUtil {
    private final HikariCPDataSource hikariCPDataSource;

    /**
     * Creates and configures a "SessionFactory" to work with "Hibernate".
     * @return configured "SessionFactory"
     */
    public SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(Grooup.class);
            configuration.addAnnotatedClass(RecordBook.class);
            configuration.addAnnotatedClass(Student.class);

            Properties hibernateProperties = new Properties();
            hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL10Dialect");
            hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
            hibernateProperties.put("hibernate.show_sql", "true");
            hibernateProperties.put("hibernate.format_sql", "true");

            hibernateProperties.put("hibernate.connection.datasource", hikariCPDataSource.getDataSource());

            configuration.setProperties(hibernateProperties);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            return configuration.buildSessionFactory(serviceRegistry);

        } catch (Throwable e) {
            log.error("Initial SessionFactory creation failed. " + e);
            throw new ExceptionInInitializerError(e);
        }
    }
}
