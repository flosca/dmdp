begin;

create schema if not exists dmd;

CREATE TABLE IF NOT EXISTS dmd.users (
 id VARCHAR(20) PRIMARY KEY,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 last_login TIME,
 is_active BOOLEAN,
 pass VARCHAR(100));

commit;
