USE shopping_mall;
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users`
(
    `member_number` INT UNSIGNED     NOT NULL AUTO_INCREMENT,
    `id`            CHAR(20)         NOT NULL,
    `password`      BINARY(32)       NOT NULL,
    `name`          CHAR(20)         NOT NULL,
    `address`       VARCHAR(255)     NOT NULL,
    `sex`           TINYINT UNSIGNED NOT NULL,
    `phone_number`  CHAR(13)         NOT NULL,
    `age`           TINYINT UNSIGNED NOT NULL,
    `point`         INT UNSIGNED     NOT NULL,
    PRIMARY KEY (`member_number`),
    UNIQUE KEY `id` (`id`)
)