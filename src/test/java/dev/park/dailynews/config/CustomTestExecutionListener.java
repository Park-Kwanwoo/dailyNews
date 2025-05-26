package dev.park.dailynews.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.List;

public class CustomTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(TestContext testContext) {
        JdbcTemplate jdbcTemplate = getJdbcTemplate(testContext);
        List<String> truncateQueries = getTruncateQueries(jdbcTemplate);
        truncateTables(jdbcTemplate, truncateQueries);
    }

    private List<String> getTruncateQueries(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList("SELECT CONCAT('TRUNCATE TABLE ', TABLES.TABLE_NAME, ';') AS q FROM INFORMATION_SCHEMA.TABLES WHERE TABLES.TABLE_SCHEMA='public'", String.class);
    }

    private JdbcTemplate getJdbcTemplate(TestContext testContext) {
        return testContext.getApplicationContext().getBean(JdbcTemplate.class);
    }

    private void truncateTables(JdbcTemplate jdbcTemplate, List<String> truncateQueries) {
        execute(jdbcTemplate, "SET REFERENTIAL_INTEGRITY FALSE");
        truncateQueries.forEach(v -> execute(jdbcTemplate, v));
        execute(jdbcTemplate, "SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void execute(JdbcTemplate jdbcTemplate, String query) {
        jdbcTemplate.execute(query);
    }
}
