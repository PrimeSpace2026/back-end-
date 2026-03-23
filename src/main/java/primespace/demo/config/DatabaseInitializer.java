package primespace.demo.config;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run(String... args) {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id BIGSERIAL PRIMARY KEY,
                email VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                name VARCHAR(255) NOT NULL,
                reset_token VARCHAR(255),
                reset_token_expiry BIGINT
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tour (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(255),
                description VARCHAR(1000),
                category VARCHAR(255),
                image_url VARCHAR(255),
                surface DOUBLE PRECISION,
                tour_url VARCHAR(255),
                latitude DOUBLE PRECISION,
                longitude DOUBLE PRECISION
            )
        """);

        // Add columns if they don't exist (for existing tables)
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION"); } catch (Exception ignored) {}
    }
}
