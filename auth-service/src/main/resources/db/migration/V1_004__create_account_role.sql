-- V4: Create account_role join table (matches Account entity @JoinTable)

CREATE TABLE IF NOT EXISTS `account_role` (
  `account_id` CHAR(36) NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`account_id`, `role_id`),
  CONSTRAINT `fk_account_role_account` FOREIGN KEY (`account_id`) REFERENCES `accounts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_account_role_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
