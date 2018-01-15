CREATE TABLE IF NOT EXISTS user (
  `id`               BIGINT             NOT NULL,
  created_by         VARCHAR(255),
  created_date       DATETIME,
  last_modified_by   VARCHAR(255),
  last_modified_date DATETIME,
  `name`             VARCHAR(255),
  `passwordless`     BIGINT             NOT NULL,
  `challenge`        BIGINT             NOT NULL,
  `sessionid`        VARCHAR(255),
  `sessionstatus`    VARCHAR(255),      NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY `UNIQUE_USER_NAME` (`name`)
);

CREATE TABLE IF NOT EXISTS user_seq (
  next_val BIGINT(20) NULL DEFAULT NULL
); 
