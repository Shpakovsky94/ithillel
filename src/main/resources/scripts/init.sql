DROP
DATABASE IF EXISTS test;
CREATE
DATABASE IF NOT EXISTS  test;

USE
test;

DROP TABLE IF EXISTS CITY;
DROP TABLE IF EXISTS COUNTRY;
DROP TABLE IF EXISTS PERSON;

CREATE TABLE COUNTRY
(
    id         INT         NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    population INT,
    currency   VARCHAR(10),
    PRIMARY KEY (id)
);

CREATE TABLE CITY
(
    id         INT         NOT NULL AUTO_INCREMENT,
    name       VARCHAR(50) NOT NULL,
    population INT,
    country_id INT         NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (country_id) REFERENCES COUNTRY (id)
);


CREATE TABLE PERSON
(
    id            INT         NOT NULL AUTO_INCREMENT,
    first_name    VARCHAR(50) NOT NULL,
    last_name     VARCHAR(50) NOT NULL,
    city_id       INT         NOT NULL,
    country_id    INT         NOT NULL,
    date_of_birth DATE,
    gender        ENUM('male', 'female', 'other'),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (country_id) REFERENCES COUNTRY (id),
    FOREIGN KEY (city_id) REFERENCES CITY (id)
);

INSERT INTO COUNTRY (name, population, currency)
VALUES ('USA', 330000000, 'USD'),
       ('Canada', 38000000, 'CAD'),
       ('Japan', 126000000, 'JPY'),
       ('Australia', 25000000, 'AUD'),
       ('France', 67000000, 'EUR');

INSERT INTO CITY (name, population, country_id)
VALUES ('New York', 8500000, 1),
       ('Toronto', 3000000, 2),
       ('Tokyo', 14000000, 3),
       ('Sydney', 5000000, 4),
       ('Paris', 2200000, 5);


INSERT INTO PERSON (first_name, last_name, city_id, country_id, date_of_birth, gender)
VALUES ('John', 'Smith', 1, 1, '1990-01-01', 'male');

-- autogenerate data to PERSON table without transaction
DROP PROCEDURE IF EXISTS sp_insert_person_no_transaction;

DELIMITER
//
CREATE PROCEDURE sp_insert_person_no_transaction()
BEGIN
    DECLARE
i INT DEFAULT 1;
    SET
autocommit = 0;

    WHILE
i <= 1000000 DO
        INSERT INTO PERSON (first_name, last_name, city_id, country_id, date_of_birth, gender)
        VALUES (
            CONCAT('First', i),
            CONCAT('Last', i),
            FLOOR(RAND() * 5) + 1,
            FLOOR(RAND() * 5) + 1,
            DATE_ADD('1970-01-01', INTERVAL FLOOR(RAND() * 36525) DAY),
            CASE FLOOR(RAND() * 3)
                WHEN 0 THEN 'male'
                WHEN 1 THEN 'female'
                ELSE 'other'
            END
        );
        SET
i = i + 1;
END WHILE;
SET
autocommit = 1;
END
//
DELIMITER ;

-- autogenerate data to PERSON table with transaction per insert
DROP PROCEDURE IF EXISTS sp_insert_person_with_transaction_per_insert;

DELIMITER
//
CREATE PROCEDURE sp_insert_person_with_transaction_per_insert()
BEGIN
    DECLARE
i INT DEFAULT 1;
    SET
autocommit = 0;

    WHILE
i <= 30000 DO
start transaction;
INSERT INTO PERSON (first_name, last_name, city_id, country_id, date_of_birth, gender)
VALUES (CONCAT('First', i),
        CONCAT('Last', i),
        FLOOR(RAND() * 5) + 1,
        FLOOR(RAND() * 5) + 1,
        DATE_ADD('1970-01-01', INTERVAL FLOOR(RAND() * 36525) DAY),
        CASE FLOOR(RAND() * 3)
            WHEN 0 THEN 'male'
            WHEN 1 THEN 'female'
            ELSE 'other'
            END);
SET
i = i + 1;
commit;
END WHILE;
SET
autocommit = 1;
END
//
DELIMITER ;

-- autogenerate data to PERSON table with transaction at the beginning
DROP PROCEDURE IF EXISTS sp_insert_person_with_transaction_at_start;

DELIMITER
//
CREATE PROCEDURE sp_insert_person_with_transaction_at_start()
BEGIN
    DECLARE
i INT DEFAULT 1;
    SET
autocommit = 0;
!!!!
start transaction;
WHILE
i <= 1000000 DO
INSERT INTO PERSON (first_name, last_name, city_id, country_id, date_of_birth, gender)
VALUES (
           CONCAT('First', i),
           CONCAT('Last', i),
           FLOOR(RAND() * 5) + 1,
           FLOOR(RAND() * 5) + 1,
           DATE_ADD('1970-01-01', INTERVAL FLOOR(RAND() * 36525) DAY),
           CASE FLOOR(RAND() * 3)
               WHEN 0 THEN 'male'
               WHEN 1 THEN 'female'
               ELSE 'other'
               END
       );
SET
i = i + 1;
END WHILE;
commit;
SET
autocommit = 1;
END
//
DELIMITER ;
