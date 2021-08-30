-- Licensed to the Apache Software Foundation (ASF) under one or more
-- contributor license agreements.  See the NOTICE file distributed with
-- this work for additional information regarding copyright ownership.
-- The ASF licenses this file to You under the Apache License, Version 2.0
-- (the "License"); you may not use this file except in compliance with
-- the License.  You may obtain a copy of the License at
--    http://www.apache.org/licenses/LICENSE-2.0
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.

DROP TABLE IF EXISTS `registered_models`;
CREATE TABLE `registered_models` (
	`name` VARCHAR(256) NOT NULL,
	`creation_time` BIGINT,
	`last_updated_time` BIGINT,
	`description` VARCHAR(5000),
	CONSTRAINT `registered_models_pk` PRIMARY KEY (`name`),
	UNIQUE (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `registered_model_tags`;
CREATE TABLE `registered_model_tags` (
	`name` VARCHAR(256) NOT NULL,
	`tag` VARCHAR(256) NOT NULL,
	CONSTRAINT `registered_model_tag_pk` PRIMARY KEY (`name`, `tag`),
	FOREIGN KEY(`name`) REFERENCES `registered_models` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `model_versions`;
CREATE TABLE `model_versions` (
	`name` VARCHAR(256) NOT NULL,
	`version` INTEGER NOT NULL,
	`user_id` VARCHAR(64) NOT NULL COMMENT 'Id of the created user',
	`experiment_id` VARCHAR(64) NOT NULL,
	`current_stage` VARCHAR(20) COMMENT 'Model stage ex: None, production...',
	`creation_time` BIGINT,
	`last_updated_time` BIGINT,
	`source` VARCHAR(512) COMMENT 'Model saved link',
	`dataset` VARCHAR(256) COMMENT 'Which dataset is used',
	`description` VARCHAR(5000),
	CONSTRAINT `model_version_pk` PRIMARY KEY (`name`, `version`),
	FOREIGN KEY(`name`) REFERENCES `registered_models` (`name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `model_version_tags`;
CREATE TABLE `model_version_tags` (
	`name` VARCHAR(256) NOT NULL,
	`version` INTEGER NOT NULL,
	`tag` VARCHAR(256) NOT NULL,
	CONSTRAINT `model_version_tag_pk` PRIMARY KEY (`name`, `version`, `tag`),
	FOREIGN KEY(`name`, `version`) REFERENCES `model_versions` (`name`, `version`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `metrics`;
CREATE TABLE `metrics` (
  `id` varchar(64) NOT NULL COMMENT 'Id of the Experiment',
  `key` varchar(190) NOT NULL COMMENT 'Metric key: `String` (limit 190 characters). Part of *Primary Key* for ``metrics`` table.',
  `value` float NOT NULL COMMENT 'Metric value: `Float`. Defined as *Non-null* in schema.',
  `worker_index` varchar(32) NOT NULL COMMENT 'Metric worker_index: `String` (limit 32 characters). Part of *Primary Key* for\r\n    ``metrics`` table.',
  `timestamp` bigint(20) NOT NULL COMMENT 'Timestamp recorded for this metric entry: `BigInteger`. Part of *Primary Key* for   ``metrics`` table.',
  `step` bigint(11) NOT NULL COMMENT 'Step recorded for this metric entry: `BigInteger`.',
  `is_nan` BOOLEAN NOT NULL COMMENT 'True if the value is in fact NaN.',
  PRIMARY KEY  (`id`, `key`, `timestamp`, `worker_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `params`;
CREATE TABLE `params` (
  `id` varchar(64) NOT NULL COMMENT 'Id of the Experiment',
  `key` varchar(190) NOT NULL COMMENT '`String` (limit 190 characters). Part of *Primary Key* for ``params`` table.',
  `value` varchar(32) NOT NULL COMMENT '`String` (limit 190 characters). Defined as *Non-null* in schema.',
  `worker_index` varchar(32) NOT NULL COMMENT '`String` (limit 32 characters). Part of *Primary Key* for\r\n    ``metrics`` table.',
  PRIMARY KEY  (`id`, `key`, `worker_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


