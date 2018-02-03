CREATE TABLE IF NOT EXISTS user (
  `id`               BIGINT
  created_by         VARCHAR(255),
  created_date       DATETIME,
  last_modified_by   VARCHAR(255),
  last_modified_date DATETIME,
  `name`             VARCHAR(255)		NOT NULL,
  `passwordless`     VARCHAR(600),      NOT NULL,
  `secret`     VARCHAR(600),  
  `sstatus`    VARCHAR(255),
  PRIMARY KEY (id),
  UNIQUE KEY `UNIQUE_USER_NAME` (`name`)
);

CREATE TABLE IF NOT EXISTS user_seq (
  next_val BIGINT(20) NULL DEFAULT NULL
); 
