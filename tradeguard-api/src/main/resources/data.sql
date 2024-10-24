CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
                                     password VARCHAR(255) NOT NULL,
                                     enabled BOOLEAN NOT NULL,
                                     role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
                                        customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        email VARCHAR(100),
                                        phone_number VARCHAR(20),
                                        created_at VARCHAR(20),
                                        user_id BIGINT,
                                        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS asset (
                                     asset_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     asset_name VARCHAR(100) NOT NULL,
                                     size DOUBLE NOT NULL,
                                     usable_size DOUBLE NOT NULL,
                                     customer_id BIGINT,
                                     FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE
);

INSERT INTO users (username, password, enabled, role) VALUES ('admin', '{noop}admin123', true, 'ADMIN');
INSERT INTO users (username, password, enabled, role) VALUES ('omer', '{noop}omer123', true, 'USER');
INSERT INTO users (username, password, enabled, role) VALUES ('ebru', '{noop}ebru456', true, 'USER');

INSERT INTO customer (name, email, phone_number, created_at, user_id)
VALUES ('Admin User', 'admin@example.com', '123456789', '2024-10-22', (SELECT id FROM users WHERE username = 'admin'));

INSERT INTO customer (name, email, phone_number, created_at, user_id)
VALUES ('Omer ALTAN', 'omer@example.com', '987654321', '2024-10-22', (SELECT id FROM users WHERE username = 'omer'));

INSERT INTO customer (name, email, phone_number, created_at, user_id)
VALUES ('Ebru ALTAN', 'ebru@example.com', '456789123', '2024-10-22', (SELECT id FROM users WHERE username = 'ebru'));

INSERT INTO asset (asset_name, size, usable_size, customer_id)
VALUES
    ('Tesla Inc. Stock', 50.0, 50.0, (SELECT customer_id FROM customer WHERE name = 'Omer ALTAN')),
    ('Ethereum', 10.0, 10.0, (SELECT customer_id FROM customer WHERE name = 'Omer ALTAN')),
    ('Amazon Stock', 30.0, 30.0, (SELECT customer_id FROM customer WHERE name = 'Omer ALTAN')),

    ('Google Inc. Stock', 20.0, 20.0, (SELECT customer_id FROM customer WHERE name = 'Ebru ALTAN')),
    ('Litecoin', 15.0, 15.0, (SELECT customer_id FROM customer WHERE name = 'Ebru ALTAN')),
    ('Netflix Stock', 25.0, 25.0, (SELECT customer_id FROM customer WHERE name = 'Ebru ALTAN'));
