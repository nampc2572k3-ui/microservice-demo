-- V1: Create roles table

CREATE TABLE IF NOT EXISTS `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `desc` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NULL,
  `created_by` VARCHAR(255) NULL,
  `updated_at` DATETIME NULL,
  `updated_by` VARCHAR(255) NULL,
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_roles_name` (`name`),
  UNIQUE KEY `uk_roles_desc` (`desc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
