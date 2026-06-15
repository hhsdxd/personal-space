CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(30) NOT NULL UNIQUE,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS borrow_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    borrow_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_time TIMESTAMP NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'BORROWED',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB;

-- 初始管理员: admin / admin123
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'ADMIN');

INSERT INTO books (title, author, isbn) VALUES
('Java编程思想', 'Bruce Eckel', '978-7-111-21250-8'),
('深入理解Java虚拟机', '周志明', '978-7-111-58585-5'),
('Spring实战', 'Craig Walls', '978-7-115-53411-8'),
('Vue.js设计与实现', '霍春阳', '978-7-115-57339-1');
