DROP DATABASE IF EXISTS test;
CREATE DATABASE test;
USE test;

CREATE TABLE IF NOT EXISTS `test`.`emoji` (
                                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                       name VARCHAR(255),
                                                       category VARCHAR(255),
                                                       `emoji_group` VARCHAR(255),
                                                       html_code VARCHAR(255)
    );
