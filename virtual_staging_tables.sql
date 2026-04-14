-- Virtual Staging tables
-- Run this on Supabase SQL Editor

CREATE TABLE IF NOT EXISTS furniture_model (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    category VARCHAR(255),
    model_url VARCHAR(2000),
    thumbnail_url VARCHAR(2000),
    default_scale DOUBLE PRECISION
);

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
);

CREATE INDEX IF NOT EXISTS idx_staged_object_tour_id ON staged_object(tour_id);
CREATE INDEX IF NOT EXISTS idx_furniture_model_category ON furniture_model(category);
