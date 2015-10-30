begin;

create schema if not exists dmd;

CREATE TABLE IF NOT EXISTS dmd.users (
 id serial,
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 email VARCHAR(30),
 admin BOOLEAN,
 pass VARCHAR(100)),
 primary key(id);


create table if not exists dmd.user_to_author (
	user_id integer,
	author_id integer,
  primary key (user_id),
  foreign key (user_id) references dmd.users(id),
  foreign key (author_id) references dmd.authors(id));

commit;
