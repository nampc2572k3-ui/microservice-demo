CREATE TABLE roles
(
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    is_system   BOOLEAN     NOT NULL,

    created_at  DATETIME,
    created_by  VARCHAR(255),
    updated_at  DATETIME,
    updated_by  VARCHAR(255),
    is_deleted  BOOLEAN,

    PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;