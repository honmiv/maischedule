
CREATE DATABASE IF NOT EXISTS `users`;

USE `users`;

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `chat_id` int(11) NOT NULL,
  `group` varchar(20) DEFAULT NULL,
  `admin` tinyint(4) NOT NULL DEFAULT '0',
  `firstName` varchar(35) DEFAULT NULL,
  `lastName` varchar(35) DEFAULT NULL,
  `userName` varchar(35) DEFAULT NULL,
  `lastAccess` timestamp NULL DEFAULT NULL,
  `pechatClick` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=784 DEFAULT CHARSET=utf8;

