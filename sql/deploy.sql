/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Cedric Chappert
 * Created: 27 juil. 2023
 */

-- Ne pas oublier de mettre le credential dans la BDD de bimi
-- Le sip serveur doit se loguer auprès de la Queue JMS pour récupérer les notifs
use bimi;

INSERT INTO bimi.Credential
  (credentialType, credentialsExpired, dateCreation, dateModification, deletable, deleted, enabled, expired, locked, password, updatable, username) 
VALUES ('client', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false, false, true, false, false, 'MDP_BCRYPT', false, 'sip-server')
ON DUPLICATE KEY UPDATE dateCreation = dateCreation;

-- Database pour SIP
create database sip default character set utf8mb4 COLLATE utf8mb4_bin;
create user sip@'%' identified by '_sip-31';
grant all privileges on sip.* TO sip@'%';

use sip;

CREATE TABLE `CONFIGURATION` (
  `configuration` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) COLLATE utf8mb3_bin NOT NULL,
  `value` varchar(255) COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`configuration`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `CONFIGURATION` VALUES 
	(1,'OAUTH_TOKEN_ENDPOINT','http://localhost:8080/bimi-authorization/oauth/check_token'),
	(2,'OAUTH_CLIENT_NAME','sip-server'),
	(3,'OAUTH_CLIENT_PASSWORD','123'),											-- Le mdp à mettre en Brypt en ligne 17
	(4,'OAUTH_RECEIVE_TOKEN_ENDPOINT','http://localhost:8080/bimi-authorization/oauth/token'),
	(5,'PUBLISH_USER','glassfishqueue');

CREATE TABLE `SIP_MESSAGES` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dateCreation` datetime NOT NULL,
  `eventId` varchar(1028) COLLATE utf8mb3_bin NOT NULL,
  `eventName` varchar(1028) COLLATE utf8mb3_bin NOT NULL,
  `message` varchar(1028) COLLATE utf8mb3_bin NOT NULL,
  `username` varchar(256) COLLATE utf8mb3_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
