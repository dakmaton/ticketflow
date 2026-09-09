ALTER TABLE events
    ALTER COLUMN currency TYPE VARCHAR(3)
    USING TRIM(currency);

ALTER TABLE events
    ADD CONSTRAINT chk_event_currency_format
    CHECK (currency ~ '^[A-Z]{3}$');