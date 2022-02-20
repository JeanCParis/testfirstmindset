CREATE TABLE IF NOT EXISTS t_account (
  id varchar2(255 byte) not null,
  username varchar2(255 byte),
  balance integer default 0
);