CREATE DATABASE IF NOT EXISTS bank;
USE bank;

CREATE TABLE IF NOT EXISTS T_ACCOUNT (
  ID varchar(255) not null,
  USERNAME varchar(255) not null,
  BALANCE integer default 0
);