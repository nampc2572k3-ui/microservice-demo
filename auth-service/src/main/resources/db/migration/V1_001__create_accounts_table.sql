CREATE TABLE accounts
(
    id            VARCHAR(36) PRIMARY KEY,

    email         VARCHAR(255) NOT NULL UNIQUE,
    username      VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    phone         VARCHAR(20)  NOT NULL UNIQUE,

    kyc_verified  BOOLEAN DEFAULT FALSE,

    last_login_at DATETIME,

    enabled       BOOLEAN      NOT NULL,
    locked        BOOLEAN      NOT NULL,

    created_at    DATETIME,
    created_by    VARCHAR(255),
    updated_at    DATETIME,
    updated_by    VARCHAR(255),
    is_deleted    BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_email ON accounts (email);
CREATE INDEX idx_username ON accounts (username);