-- liquibase formatted sql

-- changeset Salavat:session-1-CT
DROP TABLE IF EXISTS session CASCADE;
CREATE TABLE IF NOT EXISTS session
(
    id            BIGSERIAL,
    expired       BOOLEAN,
    fingerprint   VARCHAR(255),
    refresh_token VARCHAR(255),
    revoked       BOOLEAN,
    user_id       BIGINT,
    FOREIGN KEY (user_id)
        REFERENCES _user (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
-- rollback DROP TABLE IF EXISTS session CASCADE;