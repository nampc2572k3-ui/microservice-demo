-- V2: Create menus table

CREATE TABLE IF NOT EXISTS `menus` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NULL,
  `parent_id` BIGINT NULL,
  `created_at` DATETIME NULL,
  `created_by` VARCHAR(255) NULL,
  `updated_at` DATETIME NULL,
  `updated_by` VARCHAR(255) NULL,
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menus_code` (`code`),
  CONSTRAINT `fk_menus_parent` FOREIGN KEY (`parent_id`) REFERENCES `menus` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
