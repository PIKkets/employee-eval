package com.evaluation.employee_eval.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

@Configuration
public class DatabaseInitConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            try (Connection conn = dataSource.getConnection();
                 ResultSet rs = conn.getMetaData().getTables(null, null, "DEPARTMENT", null)) {
                // If the DEPARTMENT table does not exist, we assume it's an empty DB.
                if (!rs.next()) {
                    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                    populator.addScript(new ClassPathResource("schema.sql"));
                    populator.addScript(new ClassPathResource("data.sql"));
                    populator.execute(dataSource);
                    System.out.println("========== Database Initialized with schemas and dummy data ==========");
                } else {
                    System.out.println("========== Database exists, skipping schema & data initialization ==========");
                }
            }
        };
    }
}
