CREATE TABLE menus
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    code       VARCHAR(255),
    name       VARCHAR(255),
    sort_order INT,
    is_active  BOOLEAN,
    parent_id  BIGINT,

    created_at DATETIME,
    created_by VARCHAR(255),
    updated_at DATETIME,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN,

    PRIMARY KEY (id),

    CONSTRAINT uq_menus_code UNIQUE (code),

    CONSTRAINT fk_menu_parent
        FOREIGN KEY (parent_id)
            REFERENCES menus (id),

    INDEX      idx_menus_parent (parent_id),
    INDEX      idx_menus_active (is_active)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;