CREATE TABLE account_devices
(
    id             VARCHAR(255) PRIMARY KEY,

    device_name    VARCHAR(255),

    platform       VARCHAR(50),

    push_token     VARCHAR(255),

    trusted        BOOLEAN DEFAULT FALSE,

    last_active_at DATETIME,

    acc_id         VARCHAR(36),

    created_at     DATETIME,
    created_by     VARCHAR(255),
    updated_at     DATETIME,
    updated_by     VARCHAR(255),
    is_deleted     BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_device_account
        FOREIGN KEY (acc_id)
            REFERENCES accounts (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_account_id ON account_devices (acc_id);