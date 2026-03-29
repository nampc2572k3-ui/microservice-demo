CREATE TABLE refresh_tokens
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,

    account_id VARCHAR(36)  NOT NULL,

    jti        VARCHAR(255) NOT NULL UNIQUE,

    device_id  BIGINT,

    user_agent VARCHAR(255),

    ip_address VARCHAR(45),

    expires_at DATETIME     NOT NULL,

    created_at DATETIME,
    created_by VARCHAR(255),
    updated_at DATETIME,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_refresh_account
        FOREIGN KEY (account_id)
            REFERENCES accounts (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_account_id
    ON refresh_tokens (account_id);

CREATE INDEX idx_expires_at
    ON refresh_tokens (expires_at);