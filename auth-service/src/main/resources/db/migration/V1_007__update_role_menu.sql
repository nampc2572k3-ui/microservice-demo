-- remove uk_role_menu_unique
CREATE INDEX idx_role_menu_role_id ON role_menu(role_id);
CREATE INDEX idx_role_menu_menu_id ON role_menu(menu_id);

ALTER TABLE `role_menu` DROP INDEX `uk_role_menu_unique`;