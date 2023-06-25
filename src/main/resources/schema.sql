DROP DATABASE IF EXISTS ithillel_full;
CREATE DATABASE ithillel_full;
USE ithillel_full;

CREATE TABLE IF NOT EXISTS `ithillel_full`.`emoji` (
                                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                       name VARCHAR(255),
                                                       category VARCHAR(255),
                                                       `emoji_group` VARCHAR(255),
                                                       html_code VARCHAR(255)
    );
