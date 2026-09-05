CREATE TABLE events (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    venue VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sales_start_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sales_end_at TIMESTAMP WITH TIME ZONE NOT NULL,
    capacity INTEGER NOT NULL,
    base_price NUMERIC(12, 2) NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT chk_event_dates
        CHECK (end_at > start_at),

    CONSTRAINT chk_event_sales_dates
        CHECK (
            sales_end_at > sales_start_at
            AND sales_end_at <= start_at
        ),

    CONSTRAINT chk_event_capacity
        CHECK (capacity > 0),

    CONSTRAINT chk_event_base_price
        CHECK (base_price >= 0),

    CONSTRAINT chk_event_status
        CHECK (
            status IN (
                'DRAFT',
                'PUBLISHED',
                'CANCELLED',
                'COMPLETED'
            )
        )
);