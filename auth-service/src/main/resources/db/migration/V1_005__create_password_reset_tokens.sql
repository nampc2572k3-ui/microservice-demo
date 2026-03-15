CREATE TABLE password_reset_tokens
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,

    token_hash VARCHAR(255) NOT NULL UNIQUE,

    used       BOOLEAN DEFAULT FALSE,

    expires_at DATETIME,

    account_id VARCHAR(36),

    created_at DATETIME,
    created_by VARCHAR(255),
    updated_at DATETIME,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_password_reset_account
        FOREIGN KEY (account_id)
            REFERENCES accounts (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_account_id
    ON password_reset_tokens (account_id);