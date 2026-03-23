CREATE TABLE role_menus
(
    id      BIGINT NOT NULL AUTO_INCREMENT,

    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,

    bitmask INT,

    PRIMARY KEY (id),

    CONSTRAINT uq_role_menu
        UNIQUE (role_id, menu_id),

    CONSTRAINT fk_role_menu_role
        FOREIGN KEY (role_id)
            REFERENCES roles (id),

    CONSTRAINT fk_role_menu_menu
        FOREIGN KEY (menu_id)
            REFERENCES menus (id),

    INDEX   idx_role_menu_role (role_id),
    INDEX   idx_role_menu_menu (menu_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;