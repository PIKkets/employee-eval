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
import java.sql.Statement;

@Configuration
public class DatabaseInitConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                boolean hasData = false; // FORCE RESET

                if (!hasData) {
                    System.out.println("========== Initializing database schema and data ==========");
                    dropConstraintIfExists(stmt, "FK_EVAL_SCORE_MAP",    "foreign_keys",    "EVALUATION_SCORE");
                    dropConstraintIfExists(stmt, "FK_EVAL_SCORE_ELEM",   "foreign_keys",    "EVALUATION_SCORE");
                    dropConstraintIfExists(stmt, "FK_EVAL_MAP_EVALUATEE","foreign_keys",    "EVALUATOR_MAPPING");
                    dropConstraintIfExists(stmt, "FK_EVAL_MAP_EVALUATOR","foreign_keys",    "EVALUATOR_MAPPING");
                    dropConstraintIfExists(stmt, "FK_EVAL_EMP_DEPT",     "foreign_keys",    "EMPLOYEE");
                    dropConstraintIfExists(stmt, "UQ_EVAL_EMP_LOGIN",    "key_constraints", "EMPLOYEE");
                    dropConstraintIfExists(stmt, "FK_DEPT_PARENT",       "foreign_keys",    "DEPARTMENT");
                    dropConstraintIfExists(stmt, "FK_DEPT_HEAD",         "foreign_keys",    "DEPARTMENT");
                    dropTableIfExists(stmt, "EVALUATION_TYPE_WEIGHT");
                    dropTableIfExists(stmt, "EVALUATION_SCORE");
                    dropTableIfExists(stmt, "EVALUATOR_MAPPING");
                    dropTableIfExists(stmt, "EVALUATION_ELEMENT");
                    dropTableIfExists(stmt, "EMPLOYEE");
                    dropTableIfExists(stmt, "DEPARTMENT");

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

    private boolean hasEmployeeData(Statement stmt) throws Exception {
        try (ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM sys.tables WHERE name='EMPLOYEE' AND schema_id=SCHEMA_ID('dbo')")) {
            rs.next();
            if (rs.getInt(1) == 0) return false;
        }
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM dbo.EMPLOYEE")) {
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    /** 해당 테이블에 속한 제약조건만 확인 후 DROP */
    private void dropConstraintIfExists(Statement stmt, String constraintName,
                                        String sysView, String tableName) throws Exception {
        // OBJECT_ID('dbo.TABLE')로 테이블 귀속 확인 (다른 스키마의 동명 FK 혼용 방지)
        String check = String.format(
                "SELECT COUNT(*) FROM sys.%s WHERE name='%s' AND parent_object_id=OBJECT_ID('dbo.%s')",
                sysView, constraintName, tableName);
        try (ResultSet rs = stmt.executeQuery(check)) {
            rs.next();
            if (rs.getInt(1) > 0) {
                stmt.execute(String.format(
                        "ALTER TABLE dbo.%s DROP CONSTRAINT %s", tableName, constraintName));
            }
        }
    }

    private void dropTableIfExists(Statement stmt, String tableName) throws Exception {
        String check = String.format(
                "SELECT COUNT(*) FROM sys.tables WHERE name='%s' AND schema_id=SCHEMA_ID('dbo')", tableName);
        try (ResultSet rs = stmt.executeQuery(check)) {
            rs.next();
            if (rs.getInt(1) > 0) {
                stmt.execute("DROP TABLE dbo." + tableName);
            }
        }
    }
}
