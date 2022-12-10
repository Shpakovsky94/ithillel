CREATE DATABASE IF NOT EXISTS  test;

CREATE TABLE IF NOT EXISTS test.PERSON2 (
                                       PK_PERSON_ID int NOT NULL AUTO_INCREMENT,
                                       FIRST_NAME varchar(40),
                                       LAST_NAME varchar(40),
                                       AGE int,
                                       CITY varchar(20),
                                       PRIMARY KEY (PK_PERSON_ID)
                                       );

INSERT INTO test.PERSON2 (FIRST_NAME, LAST_NAME, AGE, CITY)
VALUES ('Petro', 'Petryk', 28, 'Lviv'),
       ('Zahar', 'Petryk', 26, 'Lviv');