-- liquibase formatted sql

-- changeset Salavat:_user-1-CT
DROP TABLE IF EXISTS _user CASCADE;
CREATE TABLE IF NOT EXISTS _user
(
    id         BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name  VARCHAR(255),
    password   VARCHAR(60),
    email      VARCHAR(255) UNIQUE,
    role_id    BIGINT,
    FOREIGN KEY (role_id)
        REFERENCES role (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
-- rollback DROP TABLE IF EXISTS _user CASCADE;


-- changeset Salavat:_user-2-IR
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM _user;
INSERT INTO _user(first_name, last_name, password, email, role_id)
VALUES ('Салават', 'Сейтбек', '$2a$10$gFvy5vsM7O1/0n4uF0ryaORzcEeNr6o8tnPx8HpO.Vu479c0H/NJ2', 'my@gmail.com', 1),
       ('Хамиль', 'Кошанло', '$2a$10$gFvy5vsM7O1/0n4uF0ryaORzcEeNr6o8tnPx8HpO.Vu479c0H/NJ2', 'you@gmail.com', 2);
/* все пароли - string */
-- rollback DELETE FROM _user WHERE true;