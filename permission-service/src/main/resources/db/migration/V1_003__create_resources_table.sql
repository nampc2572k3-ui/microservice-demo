CREATE TABLE resources
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,

    path_pattern VARCHAR(255) NOT NULL,
    http_method  VARCHAR(10)  NOT NULL,

    description  VARCHAR(255),
    is_active    BOOLEAN,
    action       VARCHAR(50)  NOT NULL,

    created_at   DATETIME,
    created_by   VARCHAR(255),
    updated_at   DATETIME,
    updated_by   VARCHAR(255),
    is_deleted   BOOLEAN,

    PRIMARY KEY (id),

    CONSTRAINT uq_resource_api
        UNIQUE (http_method, path_pattern),

    INDEX        idx_resource_active (is_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;