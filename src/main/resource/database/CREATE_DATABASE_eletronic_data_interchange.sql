-- --------------------------------------------------------
-- Servidor:                     172.20.233.240
-- Versão do servidor:           5.5.42 - MySQL Community Server (GPL) by Remi
-- OS do Servidor:               Linux
-- HeidiSQL Versão:              9.5.0.5295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Copiando estrutura do banco de dados para eletronic_data_interchange
CREATE DATABASE IF NOT EXISTS `eletronic_data_interchange` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `eletronic_data_interchange`;

-- Copiando estrutura para tabela eletronic_data_interchange.consumption
CREATE TABLE IF NOT EXISTS `consumption` (
  `consumption_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `jkp_id` int(11) unsigned NOT NULL,
  `traffic_date` datetime NOT NULL,
  `subscription` int(11) unsigned NOT NULL,
  `mailbox_id` varchar(255) NOT NULL,
  `tx_volume` int(11) unsigned NOT NULL,
  `rx_volume` int(11) unsigned NOT NULL,
  `not_volume` int(11) unsigned NOT NULL,
  `is_sent` datetime DEFAULT NULL,
  PRIMARY KEY (`consumption_id`),
  KEY `fk_consumption_file_jkp_id` (`jkp_id`),
  CONSTRAINT `fk_consumption_file_jkp_id` FOREIGN KEY (`jkp_id`) REFERENCES `jkp_file` (`jkp_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela eletronic_data_interchange.contention
CREATE TABLE IF NOT EXISTS `contention` (
  `contention_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `subscription` int(10) unsigned NOT NULL,
  `rx_id` int(10) unsigned NOT NULL,
  `tx_id` int(10) unsigned NOT NULL,
  `not_id` int(10) unsigned NOT NULL,
  `rx_amount` double unsigned NOT NULL,
  `tx_amount` double unsigned NOT NULL,
  `not_amount` double unsigned NOT NULL,
  `error_message` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `notificated_at` datetime DEFAULT NULL,
  `subscription_status` int(11) DEFAULT NULL,
  `subscription_servstatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`contention_id`),
  KEY `subscription` (`subscription`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela eletronic_data_interchange.jkp_file
CREATE TABLE IF NOT EXISTS `jkp_file` (
  `jkp_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `jkp_identification` varchar(200) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`jkp_id`),
  UNIQUE KEY `file_name` (`jkp_identification`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
-- Copiando estrutura para tabela eletronic_data_interchange.jkp_status
CREATE TABLE IF NOT EXISTS `jkp_status` (
  `status_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `jkp_id` int(10) unsigned NOT NULL,
  `status` enum('IMPORTED','FAILED') NOT NULL,
  `md5` char(32) NOT NULL COMMENT 'Hash md5 from file',
  `filename` varchar(100) NOT NULL COMMENT 'Real filename',
  `size` int(10) unsigned DEFAULT NULL COMMENT 'Size in bytes',
  `lines` int(10) unsigned DEFAULT NULL COMMENT 'Last line readed',
  `message` varchar(255) DEFAULT NULL,
  `create_at` datetime NOT NULL,
  PRIMARY KEY (`status_id`),
  KEY `fk_status_file_jkp_id` (`jkp_id`),
  CONSTRAINT `fk_status_file_jkp_id` FOREIGN KEY (`jkp_id`) REFERENCES `jkp_file` (`jkp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- Exportação de dados foi desmarcado.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
