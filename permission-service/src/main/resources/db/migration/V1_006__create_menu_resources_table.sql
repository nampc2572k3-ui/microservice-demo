CREATE TABLE menu_resources
(
    menu_id     BIGINT NOT NULL,
    resource_id BIGINT NOT NULL,

    PRIMARY KEY (menu_id, resource_id),

    CONSTRAINT uq_menu_resource
        UNIQUE (menu_id, resource_id),

    CONSTRAINT fk_menu_resource_menu
        FOREIGN KEY (menu_id)
            REFERENCES menus (id),

    CONSTRAINT fk_menu_resource_resource
        FOREIGN KEY (resource_id)
            REFERENCES resources (id),

    INDEX       idx_menu_resources_resource (resource_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;