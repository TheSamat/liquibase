-- liquibase formatted sql

-- changeset Salavat:permission-1-CT
DROP TABLE IF EXISTS permission CASCADE;
CREATE TABLE IF NOT EXISTS permission
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);
-- rollback DROP TABLE IF EXISTS permission CASCADE;


-- changeset Salavat:permission-2-IR
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM permission;
INSERT INTO permission(name)
VALUES ('admin:read'),
       ('admin:update'),
       ('admin:create'),
       ('admin:delete'),
       ('management:read'),
       ('management:update'),
       ('management:create'),
       ('management:delete');
-- rollback DELETE FROM permission WHERE true;


-- changeset Salavat:role-1-CT
DROP TABLE IF EXISTS role CASCADE;
CREATE TABLE IF NOT EXISTS role
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);
-- rollback DROP TABLE IF EXISTS role CASCADE;


-- changeset Salavat:role-2-IR
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM role;
INSERT INTO role(name)
VALUES ('ROLE_ADMIN'),
       ('ROLE_MANAGER');
-- rollback DELETE FROM role WHERE true;


-- changeset Salavat:role_permissions-1-CT
DROP TABLE IF EXISTS role_permissions CASCADE;
CREATE TABLE IF NOT EXISTS role_permissions
(
    role_id        BIGINT,
    permissions_id BIGINT,
    FOREIGN KEY (role_id)
        REFERENCES role (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (permissions_id)
        REFERENCES permission (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
-- rollback DROP TABLE IF EXISTS role_permissions CASCADE;


-- changeset Salavat:role_permissions-2-IR
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM role_permissions;
INSERT INTO role_permissions(role_id, permissions_id)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (1, 7),
       (1, 8),
       (2, 5),
       (2, 6),
       (2, 7),
       (2, 8)
-- rollback DELETE FROM role_permissions WHERE true;
























