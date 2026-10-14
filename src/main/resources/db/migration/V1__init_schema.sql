CREATE TABLE users (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL CHECK (role IN ('USER', 'ADMIN')),
    created_at TIMESTAMPTZ  NOT NULL
);

CREATE TABLE products (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku         VARCHAR(255)   NOT NULL UNIQUE,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(1000),
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    created_at  TIMESTAMPTZ    NOT NULL
);

CREATE TABLE stocks (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id        BIGINT  NOT NULL UNIQUE REFERENCES products (id),
    quantity_on_hand  INTEGER NOT NULL CHECK (quantity_on_hand >= 0),
    quantity_reserved INTEGER NOT NULL CHECK (quantity_reserved >= 0),
    version           BIGINT  NOT NULL DEFAULT 0,
    CHECK (quantity_reserved <= quantity_on_hand)
);

CREATE TABLE orders (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES users (id),
    status     VARCHAR(20) NOT NULL CHECK (status IN ('CREATED', 'CONFIRMED', 'COMPLETED', 'CANCELLED')),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_orders_user_id ON orders (user_id);

CREATE TABLE order_items (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id          BIGINT         NOT NULL REFERENCES orders (id),
    product_id        BIGINT         NOT NULL REFERENCES products (id),
    quantity          INTEGER        NOT NULL CHECK (quantity > 0),
    price_at_purchase NUMERIC(10, 2) NOT NULL CHECK (price_at_purchase >= 0),
    UNIQUE (order_id, product_id)
);

CREATE INDEX idx_order_items_product_id ON order_items (product_id);

CREATE TABLE order_status_history (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id    BIGINT      NOT NULL REFERENCES orders (id),
    from_status VARCHAR(20) CHECK (from_status IN ('CREATED', 'CONFIRMED', 'COMPLETED', 'CANCELLED')),
    to_status   VARCHAR(20) NOT NULL CHECK (to_status IN ('CREATED', 'CONFIRMED', 'COMPLETED', 'CANCELLED')),
    changed_at  TIMESTAMPTZ NOT NULL,
    changed_by  BIGINT      NOT NULL REFERENCES users (id)
);

CREATE INDEX idx_order_status_history_order_id ON order_status_history (order_id);
CREATE INDEX idx_order_status_history_changed_by ON order_status_history (changed_by);