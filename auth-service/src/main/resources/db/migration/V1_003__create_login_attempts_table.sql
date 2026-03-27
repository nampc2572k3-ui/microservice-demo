CREATE TABLE login_attempts
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,

    email          VARCHAR(255),

    ip_address     VARCHAR(45),

    successful     BOOLEAN,

    failure_reason VARCHAR(255),

    attempted_at   DATETIME,

    created_at     DATETIME,
    created_by     VARCHAR(255),
    updated_at     DATETIME,
    updated_by     VARCHAR(255),
    is_deleted     BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_email_time
    ON login_attempts (email, attempted_at);

CREATE INDEX idx_ip_time
    ON login_attempts (ip_address, attempted_at);