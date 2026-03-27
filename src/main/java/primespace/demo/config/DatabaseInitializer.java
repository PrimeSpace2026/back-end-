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
        try {
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
                longitude DOUBLE PRECISION,
                location VARCHAR(255)
            )
        """);

        // Add columns if they don't exist (for existing tables)
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS location VARCHAR(255)"); } catch (Exception ignored) {}

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tour_item (
                id BIGSERIAL PRIMARY KEY,
                tour_id BIGINT NOT NULL,
                name VARCHAR(255),
                description VARCHAR(1000),
                image_url VARCHAR(2000),
                price DOUBLE PRECISION,
                currency VARCHAR(10) DEFAULT 'EUR',
                external_url VARCHAR(2000),
                brand VARCHAR(255),
                tag_sid VARCHAR(255)
            )
        """);

        // Widen URL columns if table already existed with smaller limits
        try { jdbcTemplate.execute("ALTER TABLE tour_item ALTER COLUMN image_url TYPE VARCHAR(2000)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour_item ALTER COLUMN external_url TYPE VARCHAR(2000)"); } catch (Exception ignored) {}

        } catch (Exception e) {
            System.err.println("WARNING: DatabaseInitializer failed (DB may be temporarily unavailable): " + e.getMessage());
        }
    }
}
