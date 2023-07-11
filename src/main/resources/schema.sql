DROP DATABASE IF EXISTS test;
CREATE DATABASE test;
USE test;

CREATE TABLE IF NOT EXISTS `test`.`user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(100) NULL,
    role VARCHAR(255) NULL,
    email VARCHAR(100) NULL,
    is_premium BIT NULL,
    CONSTRAINT UK_ob8kqyqqgmefl0aco34akdtpe UNIQUE (email),
    CONSTRAINT UK_sb8bbouer5wak8vyiiy4pf2bx UNIQUE (username)
    );
