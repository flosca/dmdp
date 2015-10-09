begin;

create schema if not exists dmd;

create table if not exists dmd.authors (
	id serial,
	keyname text, -- surname or name of laboratory/etc.
	forenames text, -- name, patronym or null if keyname is a name of laboratory
  date_of_birthday date,
  affiliation text,
  primary key (id));


create table if not exists dmd.publications (
	id serial,
  uid text not null,
	title text not null,
	date_created date not null,
	date_updated date,
	journal_ref text, -- journal reference
	abstract text,
	doi text,
  isbn text,
	subcategories text,
	comments text,
  unique(uid),
  primary key (id));


create table if not exists dmd.categories (
	id serial,
	category_name text,
  primary key (id));


create table if not exists dmd.author_of (
	author_id integer,
	publication_id integer,
  primary key (author_id, publication_id),
  foreign key (author_id) references dmd.authors(id),
  foreign key (publication_id) references dmd.publications(id));


create table if not exists dmd.category_of (
	category_id integer,
	publication_id integer,
  primary key (category_id, publication_id),
  foreign key (category_id) references dmd.categories(id),
  foreign key (publication_id) references dmd.publications(id));


create table if not exists dmd.related_to (
  publication_id integer,
  related_publication_id integer,
  primary key (publication_id, related_publication_id),
  foreign key (publication_id) references dmd.publications(id),
  foreign key (related_publication_id) references dmd.publications(id));

commit;
