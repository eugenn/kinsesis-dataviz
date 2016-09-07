-- create database kinesis;
USE kinesis;

CREATE TABLE IF NOT EXISTS mapping (
  bidrequestId VARCHAR(36) NOT NULL,
  bannerId VARCHAR(36) NOT NULL,
  audienceId VARCHAR(36) NOT NULL,
  timestamp timestamp(6) NOT NULL
) ENGINE=InnoDB
