-- Replace property_amenities link-entity table with a pure ManyToMany join table.
-- Keeps existing active links and drops soft-delete/audit columns model.

CREATE TABLE IF NOT EXISTS properties_amenities (
    property_id BIGINT NOT NULL,
    amenity_id  BIGINT NOT NULL,
    PRIMARY KEY (property_id, amenity_id),
    CONSTRAINT fk_properties_amenities_property
        FOREIGN KEY (property_id) REFERENCES properties(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_properties_amenities_amenity
        FOREIGN KEY (amenity_id) REFERENCES amenities(id)
            ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_properties_amenities_property
    ON properties_amenities(property_id);

CREATE INDEX IF NOT EXISTS idx_properties_amenities_amenity
    ON properties_amenities(amenity_id);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'property_amenities'
    ) THEN
        INSERT INTO properties_amenities (property_id, amenity_id)
        SELECT pa.property_id, pa.amenity_id
        FROM property_amenities pa
        WHERE COALESCE(pa.deleted, FALSE) = FALSE
        ON CONFLICT (property_id, amenity_id) DO NOTHING;

        DROP TABLE property_amenities;
    END IF;
END $$;
