begin;

create schema if not exists dmd;

CREATE TABLE IF NOT EXISTS dmd.users (
 id serial,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 pass VARCHAR(100));

commit;
