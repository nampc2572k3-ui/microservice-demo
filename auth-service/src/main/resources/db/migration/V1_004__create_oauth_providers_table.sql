CREATE TABLE oauth_providers
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,

    provider         VARCHAR(50),

    provider_uid     VARCHAR(255),

    access_token     TEXT,

    token_expires_at DATETIME,

    account_id       VARCHAR(36),

    created_at       DATETIME,
    created_by       VARCHAR(255),
    updated_at       DATETIME,
    updated_by       VARCHAR(255),
    is_deleted       BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_oauth_account
        FOREIGN KEY (account_id)
            REFERENCES accounts (id)
            ON DELETE CASCADE,

    CONSTRAINT uq_provider_uid
        UNIQUE (provider, provider_uid)
);