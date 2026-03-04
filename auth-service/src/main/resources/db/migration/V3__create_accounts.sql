-- V3: Create accounts table

CREATE TABLE IF NOT EXISTS `accounts` (
  `id` CHAR(36) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `enabled` BOOLEAN NOT NULL DEFAULT FALSE,
  `locked` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` DATETIME NULL,
  `created_by` VARCHAR(255) NULL,
  `updated_at` DATETIME NULL,
  `updated_by` VARCHAR(255) NULL,
  `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_accounts_email` (`email`),
  UNIQUE KEY `uk_accounts_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
