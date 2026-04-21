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
                image_url VARCHAR(2000),
                surface DOUBLE PRECISION,
                tour_url VARCHAR(2000),
                latitude DOUBLE PRECISION,
                longitude DOUBLE PRECISION,
                location VARCHAR(500),
                metadata_json TEXT
            )
        """);

        // Add columns if they don't exist (for existing tables)
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS location VARCHAR(500)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS metadata_json TEXT"); } catch (Exception ignored) {}

        // Widen URL columns if table already existed with smaller limits
        try { jdbcTemplate.execute("ALTER TABLE tour ALTER COLUMN image_url TYPE TEXT"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ALTER COLUMN tour_url TYPE TEXT"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ALTER COLUMN location TYPE TEXT"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ALTER COLUMN description TYPE TEXT"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ALTER COLUMN metadata_json TYPE TEXT"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ALTER COLUMN name TYPE VARCHAR(500)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ALTER COLUMN category TYPE VARCHAR(200)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT TRUE"); } catch (Exception ignored) {}

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

        // Ensure chamber.booking_url column exists
        try { jdbcTemplate.execute("ALTER TABLE chamber ADD COLUMN IF NOT EXISTS booking_url VARCHAR(2000)"); } catch (Exception ignored) {}

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tour_tag (
                id BIGSERIAL PRIMARY KEY,
                tour_id BIGINT NOT NULL,
                name VARCHAR(255),
                sid VARCHAR(255)
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tour_visit (
                id BIGSERIAL PRIMARY KEY,
                tour_id BIGINT NOT NULL,
                visitor_id VARCHAR(255),
                started_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
                duration_seconds INTEGER
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tour_event (
                id BIGSERIAL PRIMARY KEY,
                tour_id BIGINT NOT NULL,
                visitor_id VARCHAR(255),
                event_type VARCHAR(50),
                target_name VARCHAR(255),
                target_id VARCHAR(255),
                created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS tour_service (
                id BIGSERIAL PRIMARY KEY,
                tour_id BIGINT NOT NULL,
                name VARCHAR(255),
                description VARCHAR(1000),
                image_url VARCHAR(2000),
                phone VARCHAR(255),
                whatsapp VARCHAR(255),
                instagram VARCHAR(500),
                facebook VARCHAR(500),
                tag_sid VARCHAR(255)
            )
        """);

        // Add browser/location columns to tour_visit
        try { jdbcTemplate.execute("ALTER TABLE tour_visit ADD COLUMN IF NOT EXISTS browser VARCHAR(255)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour_visit ADD COLUMN IF NOT EXISTS country VARCHAR(255)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour_visit ADD COLUMN IF NOT EXISTS city VARCHAR(255)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour_visit ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE tour_visit ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION"); } catch (Exception ignored) {}

        // Virtual staging tables
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS furniture_model (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(255),
                category VARCHAR(255),
                model_url VARCHAR(2000),
                thumbnail_url VARCHAR(2000),
                default_scale DOUBLE PRECISION
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS staged_object (
                id BIGSERIAL PRIMARY KEY,
                tour_id BIGINT REFERENCES tour(id) ON DELETE CASCADE,
                furniture_model_id BIGINT REFERENCES furniture_model(id) ON DELETE SET NULL,
                model_url VARCHAR(2000),
                sweep_id VARCHAR(255),
                pos_x DOUBLE PRECISION,
                pos_y DOUBLE PRECISION,
                pos_z DOUBLE PRECISION,
                rot_x DOUBLE PRECISION,
                rot_y DOUBLE PRECISION,
                rot_z DOUBLE PRECISION,
                scale_x DOUBLE PRECISION,
                scale_y DOUBLE PRECISION,
                scale_z DOUBLE PRECISION,
                label VARCHAR(255)
            )
        """);

        try { jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_staged_object_tour_id ON staged_object(tour_id)"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_furniture_model_category ON furniture_model(category)"); } catch (Exception ignored) {}

        // Add local transform columns to staged_object
        try { jdbcTemplate.execute("ALTER TABLE staged_object ADD COLUMN IF NOT EXISTS local_scale DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE staged_object ADD COLUMN IF NOT EXISTS local_offset_y DOUBLE PRECISION"); } catch (Exception ignored) {}
        try { jdbcTemplate.execute("ALTER TABLE staged_object ADD COLUMN IF NOT EXISTS local_rotation_y DOUBLE PRECISION"); } catch (Exception ignored) {}

        } catch (Exception e) {
            System.err.println("WARNING: DatabaseInitializer failed (DB may be temporarily unavailable): " + e.getMessage());
        }
    }
}
