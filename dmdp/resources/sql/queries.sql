-- name: create-user!
-- creates a new user record
INSERT INTO dmd.users
(first_name, last_name, email, admin, pass, salt)
VALUES (:first_name, :last_name, :email, false, :encrypted_pass, :salt)

-- name: update-user!
-- update an existing user record
UPDATE dmd.users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT * FROM dmd.users
WHERE id = :id

-- name: get-user-by-email
-- retreive a user given an email
SELECT * FROM dmd.users
WHERE email = :email;


-- name: delete-user!
-- delete a user given the id
DELETE FROM dmd.users
WHERE id = :id




-- name: get-authors
-- get a list of authors
SELECT * FROM dmd.authors OFFSET :offset LIMIT :limit

-- name: get-author
-- get a specific author by id
SELECT * FROM dmd.authors WHERE id = :id


-- name: get-author-by-name
SELECT distinct * FROM dmd.authors WHERE keyname = :keyname and forenames = :forenames

-- name: search-author-by-name
SELECT distinct * FROM dmd.authors WHERE keyname like :keyname or forenames like :forenames limit 10

-- name: create-author!
-- create an author
INSERT INTO dmd.authors
(keyname, forenames, date_of_birthday, affiliation)
VALUES (:keyname, :forenames, :date_of_birthday, :affiliation)

-- name: update-author!
-- update an author's info
UPDATE dmd.authors
SET keyname = :keyname, forenames = :forenames, date_of_birthday = :date_of_birthday, affiliation = :affiliation
WHERE id = :id

-- name: get-publications
-- get a list of publications
SELECT * FROM dmd.publications OFFSET :offset LIMIT :limit

-- name: get-publication
-- get a specific publication by id
SELECT * FROM dmd.publications WHERE id = :id

-- name: get-authors-of-publication
select * from dmd.authors where id in
  (SELECT author_id FROM dmd.author_of WHERE publication_id = :pub_id)

-- name: get-publications-from-category-by-category-id
select * from dmd.publications where id in
  (SELECT publication_id FROM dmd.category_of WHERE category_id =
   (select id from dmd.categories where id = :category_id)) limit :limit offset :offset

-- name: count-publications
select count(*) from dmd.publications where id in
  (SELECT publication_id FROM dmd.category_of WHERE category_id =
   (select id from dmd.categories where category_name = :cat_name))

-- name: get-publications-by-title
-- get a list of publications
SELECT * FROM dmd.publications WHERE title like :title limit 10

-- name: get-publications-by-title-from-category
-- get a list of publications
SELECT * FROM dmd.publications WHERE title like :title and id in
  (SELECT publication_id FROM dmd.category_of WHERE category_id =
   (select id from dmd.categories where id = :category_id)) limit :limit offset :offset

-- name: get-publications-by-author
-- get a list of author's publications
SELECT * FROM dmd.publications WHERE id in
  (SELECT publication_id FROM dmd.author_of WHERE author_id = :author_id)

-- name: get-publications-by-author-name
SELECT * FROM dmd.publications WHERE id in
  (SELECT publication_id FROM dmd.author_of WHERE author_id in
    (select id from dmd.authors where keyname = :keyname and forenames = :forenames))

-- name: create-publication!
-- create a publication
INSERT INTO dmd.publications
(uid, title, date_created, date_updated, journal_ref, abstract, doi, isbn, subcategories, comments)
VALUES (:uid, :title, :date_created, :date_updated, :journal_ref, :abstract, :doi, :isbn, :subcategories, :comments)

-- name: update-publication!
-- update a publication info
UPDATE dmd.publications
SET uid = :uid, title = :title, date_created = :date_created, date_updated = :date_updated,
  journal_ref = :journal_ref, abstract = :abstract, doi = :doi, isbn = :isbn,
  subcategories = :subcategories, comments = :comments
WHERE id = :id

-- name: bind-publication-to-author!
-- bind a publication to an author
INSERT INTO dmd.author_of
(author_id, publication_id)
VALUES (:author_id, :publication_id)

-- name: get-categories
-- get a list of categories available
SELECT * FROM dmd.categories

-- name: get-category-name-by-id
select category_name from dmd.categories where id = :id

-- name: get-category-id
-- get a particular category by name
select id from dmd.categories where category_name = :cat_name

-- name: get-publication-categories
select * from dmd.categories where id in
  (select category_id from dmd.category_of where publication_id = :publication_id)

-- name: create-category!
-- create a new category
INSERT INTO dmd.categories
(category_name)
VALUES (:category_name)

-- name: update-category!
-- update a specific category
UPDATE dmd.categories
SET category_name = :category_name
WHERE id = :id

