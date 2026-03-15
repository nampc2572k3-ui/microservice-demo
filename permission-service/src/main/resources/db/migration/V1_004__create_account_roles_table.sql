CREATE TABLE account_roles
(
    id         BIGINT NOT NULL AUTO_INCREMENT,

    account_id BIGINT NOT NULL,
    role_id    BIGINT NOT NULL,

    created_at DATETIME,
    created_by VARCHAR(255),
    updated_at DATETIME,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN,

    PRIMARY KEY (id),

    CONSTRAINT uq_account_role
        UNIQUE (account_id, role_id),

    CONSTRAINT fk_account_roles_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id),

    INDEX      idx_account_roles_account (account_id),
    INDEX      idx_account_roles_role (role_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;