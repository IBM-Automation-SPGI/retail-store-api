CREATE TABLE products (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    description VARCHAR(400),
    price       DECIMAL(10,2) NOT NULL,
    stock       INT NOT NULL
);

CREATE TABLE customers (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL
);

CREATE TABLE cart_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE coupons (
    code    VARCHAR(16) PRIMARY KEY,
    percent INT NOT NULL
);

CREATE TABLE orders (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    subtotal    DECIMAL(10,2) NOT NULL,
    discount    DECIMAL(10,2) NOT NULL,
    total       DECIMAL(10,2) NOT NULL,
    coupon_code VARCHAR(16),
    note        VARCHAR(1000),
    created_at  TIMESTAMP NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE order_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    name       VARCHAR(120) NOT NULL,
    quantity   INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
