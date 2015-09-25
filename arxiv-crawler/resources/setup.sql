begin;

create table if not exists authors(
	id serial,
	keyname text,
	forenames text,
  primary key (id));


create table if not exists publications(
	id serial,
  uid text not null,
	title text not null,
	date_created varchar(10) not null,
	date_updated varchar(10),
	journal_ref text,
	abstract text,
	doi text,
	category text,
	comments text,
  unique(uid),
  primary key (id));


create table if not exists specifications(
	id serial,
	specname text,
  primary key (id));


create table if not exists writes(
	author_id integer,
	publication_id integer,
  primary key (author_id, publication_id),
  foreign key (author_id) references authors(id),
  foreign key (publication_id) references publications(id));


create table if not exists includes(
	specification_id integer,
	publication_id integer,
  primary key (specification_id, publication_id),
  foreign key (specification_id) references specifications(id),
  foreign key (publication_id) references publications(id));



commit;

