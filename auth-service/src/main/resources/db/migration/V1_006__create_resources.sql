-- V6: Create resources table

CREATE TABLE IF NOT EXISTS `resources` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `menu_id` BIGINT NOT NULL,
  `method` VARCHAR(255) NOT NULL,
  `pattern` VARCHAR(255) NOT NULL,
  `action` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NULL,
  `created_by` VARCHAR(255) NULL,
  `updated_at` DATETIME NULL,
  `updated_by` VARCHAR(255) NULL,
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_resources_menu` FOREIGN KEY (`menu_id`) REFERENCES `menus` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
